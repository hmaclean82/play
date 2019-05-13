package hmac.play

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import hmac.play.data.Social
import hmac.play.injection.ApplicationComponent
import hmac.play.restAPIs.SocialDataService
import hmac.play.screens.PostsActivity
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import rx.Observable

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, application = MyApplication::class)
class PostActivitySpec {
    @MockK
    private lateinit var applicationComponent: ApplicationComponent

    @MockK
    lateinit var socialDataService: SocialDataService

    @MockK(relaxed = true)
    private lateinit var intent: Intent

    private lateinit var activityController: ActivityController<PostsActivity>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        (RuntimeEnvironment.application as MyApplication).component = applicationComponent

        every { applicationComponent.inject(any<PostsActivity>()) } answers {
            activityController.get().socialDataService = socialDataService
        }
    }

    private fun createActivity(): ActivityController<PostsActivity> =
        Robolectric.buildActivity(PostsActivity::class.java, intent)
            .also { activityController = it }

    @Test
    fun `data is fetched and shown in posts grid`() {

        every { socialDataService.postsWithComments() } returns Observable.just(Social.derivedPostsWithComments)

        createActivity().setup()

        val postsGrid = activityController.get().findViewById<RecyclerView>(R.id.posts_grid)
        val postDetails = activityController.get().findViewById<View>(R.id.post_details)
        val toolbar = activityController.get().findViewById<View>(R.id.toolbar)
        val placeholder = activityController.get().findViewById<View>(R.id.placeholder)
        val scrim = activityController.get().findViewById<View>(R.id.scrim)

        assertEquals(View.VISIBLE, postsGrid.visibility)
        assertEquals(View.GONE, postDetails.visibility)
        assertEquals(View.GONE, toolbar.visibility)
        assertEquals(View.GONE, placeholder.visibility)
        assertEquals(View.GONE, scrim.visibility)

        assertEquals(postsGrid.adapter!!.itemCount, Social.derivedPostsWithComments.size)
    }
}