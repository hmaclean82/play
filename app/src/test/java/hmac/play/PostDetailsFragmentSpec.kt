package hmac.play

import android.support.v4.app.Fragment
import android.view.View
import android.widget.TextView
import hmac.play.data.Social
import hmac.play.screens.PostDetailsFragment
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.support.v4.SupportFragmentController

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = MyApplication::class)
class PostDetailsFragmentSpec {

    private val postWithComments = Social.derivedPostsWithComments[0]

    private lateinit var fragmentController: SupportFragmentController<PostDetailsFragment>

    private fun <T : Fragment> SupportFragmentController<T>.setup() =
        this.start().resume().visible()

    @Before
    fun setUp() {

    }

    private fun createFragmentWithPost(): SupportFragmentController<PostDetailsFragment> =
        SupportFragmentController.of(PostDetailsFragment.newInstance(postWithComments))
            .also { fragmentController = it }
            .create()
            .setup()

    private fun createFragmentWithoutPost(): SupportFragmentController<PostDetailsFragment> =
        SupportFragmentController.of(PostDetailsFragment())
            .also { fragmentController = it }
            .create()
            .setup()

    @Test
    fun `given fragment created without post view is gone`() {
        createFragmentWithoutPost()

        assertEquals(View.GONE, fragmentController.get().view!!.visibility)
    }

    @Test
    fun `given fragment created with post display certain post details`() {
        createFragmentWithPost()

        val postText = fragmentController.get().view!!.findViewById<TextView>(R.id.post_text)
        val postTitle = fragmentController.get().view!!.findViewById<TextView>(R.id.post_title)
        val footer = fragmentController.get().view!!.findViewById<TextView>(R.id.footer)

        assertEquals(View.VISIBLE, postText.visibility)
        assertEquals(View.VISIBLE, postTitle.visibility)
        assertEquals(View.VISIBLE, footer.visibility)
        assertEquals(postWithComments.post.body, postText.text)
        assertEquals(postWithComments.post.title, postTitle.text)
        assertEquals("1 Comment", footer.text)
    }
}