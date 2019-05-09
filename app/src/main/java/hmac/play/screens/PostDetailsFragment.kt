package hmac.play.screens

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import hmac.play.R
import hmac.play.models.PostWithComments
import kotlinx.android.synthetic.main.post_fragment.*

class PostDetailsFragment: Fragment() {

    private lateinit var postWithComments: PostWithComments

    companion object {
        const val POST_ARG = "post_arg"

        fun create(post: PostWithComments): PostDetailsFragment{
            val fragment = PostDetailsFragment()
            fragment.arguments = Bundle()
            fragment.arguments?.putString(POST_ARG, Gson().toJson(post))
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postWithComments = requireNotNull(Gson().fromJson(arguments?.getString(POST_ARG), PostWithComments::class.java))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.post_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        post_text.text = postWithComments.post.body
        post_title.text = postWithComments.post.title
        footer.text = resources.getQuantityString(R.plurals.commentsCount, postWithComments.comments.size, postWithComments.comments.size)
    }
}