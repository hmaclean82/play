package hmac.play.screens

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import hmac.play.MyApplication
import hmac.play.R
import hmac.play.models.PostWithUsername
import hmac.play.restAPIs.SocialDataService
import hmac.play.utils.DisplayUtils
import hmac.play.utils.RxUtils
import kotlinx.android.synthetic.main.posts_activity.*
import kotlinx.android.synthetic.main.placeholder_with_button_and_spinner.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class PostsActivity : AppCompatActivity() {

    @Inject
    lateinit var socialDataService: SocialDataService

    private var subscriptions: CompositeSubscription? = null
    private var postsAdapter: PostsAdapter? = null
    private var currentState: ViewState? = null

    private sealed class ViewState {
        object Loading : ViewState()
        data class WithData(val posts: List<PostWithUsername>) : ViewState()
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
                placeholder.visibility = View.VISIBLE

                progress_spinner.visibility = View.VISIBLE
                button.visibility = View.INVISIBLE
                placeholder_text.text = getString(R.string.retrieving_data_message)
            }
            is ViewState.WithData -> {
                posts_grid.visibility = View.VISIBLE
                post_details.visibility = View.GONE
                placeholder.visibility = View.GONE

                val columns = resources.getInteger(R.integer.posts_grid_columns)
                val itemSize = DisplayUtils.screenSize(this).x / columns
                posts_grid.layoutManager = GridLayoutManager(this, columns)
                postsAdapter = PostsAdapter(state.posts, itemSize)
                posts_grid.adapter = postsAdapter
            }
            is ViewState.Empty -> {
                posts_grid.visibility = View.GONE
                post_details.visibility = View.GONE
                placeholder.visibility = View.VISIBLE

                progress_spinner.visibility = View.INVISIBLE
                placeholder_text.text = getString(R.string.no_data_message)
            }
            is ViewState.Error -> {
                posts_grid.visibility = View.GONE
                post_details.visibility = View.GONE
                placeholder.visibility = View.VISIBLE

                progress_spinner.visibility = View.INVISIBLE
                placeholder_text.text = getString(R.string.data_retrieval_error_message)
            }
        }
        currentState = state
    }

    fun fetchAndDisplayPosts() {

        socialDataService
            .postsWithUsernames()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { if (currentState !is ViewState.WithData) configure(ViewState.Loading) }
            .map<ViewState> { posts ->
                if (posts.isEmpty())
                    ViewState.Empty
                else
                    ViewState.WithData(posts)
            }
            .onErrorResumeNext {
                if (currentState is ViewState.WithData)
                    Observable.empty()
                else
                    Observable.just(ViewState.Error)
            }
            .subscribe (
                { configure(it) },
                {} )

    }

    override fun onResume() {
        super.onResume()
        RxUtils.getNewCompositeSubIfUnsubscribed(subscriptions)
        fetchAndDisplayPosts()
    }

    override fun onPause() {
        super.onPause()
        subscriptions?.clear()
    }


}
