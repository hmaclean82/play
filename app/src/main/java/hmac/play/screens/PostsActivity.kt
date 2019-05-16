package hmac.play.screens

import android.animation.ArgbEvaluator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import hmac.play.MyApplication
import hmac.play.R
import hmac.play.models.PostWithComments
import hmac.play.restAPIs.SocialDataService
import hmac.play.utils.DisplayUtils
import hmac.play.utils.RxUtils
import kotlinx.android.synthetic.main.posts_activity.*
import kotlinx.android.synthetic.main.placeholder_with_button_and_spinner.*
import kotlinx.android.synthetic.main.toolbar_transparent.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject
import android.animation.ValueAnimator

import android.support.v4.content.ContextCompat
import android.view.animation.DecelerateInterpolator


class PostsActivity : AppCompatActivity(), PostsAdapter.PostClickedListener {

    @Inject
    lateinit var socialDataService: SocialDataService

    private var subscriptions: CompositeSubscription? = null
    private var postsAdapter: PostsAdapter? = null
    private var currentState: ViewState? = null

    private sealed class ViewState {
        object Loading : ViewState()
        data class Posts(val posts: List<PostWithComments>? = null) : ViewState()
        data class PostDetails(val post: PostWithComments) : ViewState()
        object Empty : ViewState()
        object Error : ViewState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.instance.component.inject(this)
        setContentView(R.layout.posts_activity)
    }

    private fun configure(state: ViewState) {
        when(state) {
            is ViewState.Loading -> {
                posts_grid.visibility = View.GONE
                post_details.visibility = View.GONE
                scrim.visibility = View.GONE
                toolbar.visibility = View.GONE
                placeholder.visibility = View.VISIBLE

                progress_spinner.visibility = View.VISIBLE
                button.visibility = View.GONE
                placeholder_text.text = getString(R.string.retrieving_data_message)
            }
            is ViewState.Posts -> {
                posts_grid.visibility = View.VISIBLE
                post_details.visibility = View.GONE
                placeholder.visibility = View.GONE
                scrim.visibility = View.GONE
                toolbar.visibility = View.GONE

                state.posts?.let { createOrRefreshAdapter(it) }
            }
            is ViewState.PostDetails -> {
                posts_grid.visibility = View.VISIBLE
                post_details.visibility = View.VISIBLE
                scrim.visibility = View.VISIBLE
                toolbar.visibility = View.VISIBLE
                placeholder.visibility = View.GONE

                toolbar_title.text = getString(R.string.posted_by, state.post.author)
                setSupportActionBar(toolbar)
                supportActionBar?.setDisplayShowTitleEnabled(false)

                scrim.setOnClickListener { if (currentState is ViewState.PostDetails) onBackPressed() }
                fadeInScrim()

                loadPostDetailsFragment(state.post)
            }
            is ViewState.Empty -> {
                posts_grid.visibility = View.GONE
                post_details.visibility = View.GONE
                scrim.visibility = View.GONE
                placeholder.visibility = View.VISIBLE

                progress_spinner.visibility = View.GONE
                button.visibility = View.GONE
                placeholder_text.text = getString(R.string.no_data_message)
            }
            is ViewState.Error -> {
                posts_grid.visibility = View.GONE
                post_details.visibility = View.GONE
                scrim.visibility = View.GONE
                placeholder.visibility = View.VISIBLE

                progress_spinner.visibility = View.GONE
                button.visibility = View.VISIBLE
                button.setOnClickListener { fetchAndDisplayPosts() }
                placeholder_text.text = getString(R.string.data_retrieval_error_message)
            }
        }
        currentState = state
    }

    private fun createOrRefreshAdapter(posts: List<PostWithComments>) {
        postsAdapter?.also { it.update(posts) }
            ?: run {
                val columns = resources.getInteger(R.integer.posts_grid_columns)
                val itemSize = DisplayUtils.screenSize(this).x / columns
                posts_grid.layoutManager = GridLayoutManager(this, columns)
                postsAdapter = PostsAdapter(posts, itemSize)
                postsAdapter?.setListener(this)
                posts_grid.adapter = postsAdapter
            }
    }

    override fun postClicked(post: PostWithComments) {
        configure(ViewState.PostDetails(post))
    }

    private fun loadPostDetailsFragment(post: PostWithComments) {
        val fragment = PostDetailsFragment.newInstance(post)

        supportFragmentManager.beginTransaction()
            .add(R.id.post_details, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun fadeInScrim() {
        val colorFrom = ContextCompat.getColor(this, R.color.transparent)
        val colorTo = ContextCompat.getColor(this, R.color.transparent_black)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = 250
        colorAnimation.interpolator = DecelerateInterpolator()
        colorAnimation.addUpdateListener { animator -> scrim?.setBackgroundColor(animator.animatedValue as Int) }
        colorAnimation.start()
    }

    private fun fetchAndDisplayPosts() {
        fun haveDataAlready() = postsAdapter?.itemCount?.let { it > 0 } ?: false

        subscriptions?.add(
            socialDataService
                .postsWithComments()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { if (!haveDataAlready()) configure(ViewState.Loading) }
                .map<ViewState> { posts ->
                    if (posts.isEmpty())
                        ViewState.Empty
                    else
                        ViewState.Posts(posts)
                }
                .onErrorResumeNext {
                    if (haveDataAlready())
                        Observable.empty()
                    else
                        Observable.just(ViewState.Error)
                }
                .subscribe (
                    { configure(it) },
                    {} )
        )
    }

    override fun onResume() {
        super.onResume()
        subscriptions = RxUtils.getNewCompositeSubIfUnsubscribed(subscriptions)
        fetchAndDisplayPosts()
    }

    override fun onPause() {
        super.onPause()
        subscriptions?.clear()
    }

    override fun onBackPressed() {
        if (currentState is ViewState.PostDetails)
            configure(ViewState.Posts())

        super.onBackPressed()
    }

}
