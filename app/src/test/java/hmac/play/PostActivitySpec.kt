package hmac.play

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import hmac.play.data.Social
import hmac.play.injection.ApplicationComponent
import hmac.play.restAPIs.SocialDataService
import hmac.play.screens.PostsActivity
import hmac.play.screens.PostsAdapter
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import rx.Observable
import java.util.concurrent.TimeUnit

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

        mockkConstructor(PostsAdapter::class)

        (RuntimeEnvironment.application as MyApplication).component = applicationComponent

        every { applicationComponent.inject(any<PostsActivity>()) } answers {
            activityController.get().socialDataService = socialDataService
        }
    }

    @After
    fun teardown() {
        unmockkConstructor(PostsAdapter::class)
    }

    private fun createActivity(): ActivityController<PostsActivity> =
        Robolectric.buildActivity(PostsActivity::class.java, intent)
            .also { activityController = it }

    private fun createActivityAndProvideData() {
        every { socialDataService.postsWithComments() } returns Observable.just(Social.derivedPostsWithComments)
        createActivity().setup()

        //runBlocking{ delay(500) }
    }

    private fun createActivityProvideDataAndClickFirstItem() {
        createActivityAndProvideData()
        val postsGrid = activityController.get().findViewById<RecyclerView>(R.id.posts_grid)

        val firstPostView = postsGrid.getChildAt(0)
        firstPostView.performClick()
    }

    @Test
    fun `data is fetched and shown in posts grid`() {
        createActivityAndProvideData()

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

    @Test
    fun `on error with data fetch placeholder with message and button to retry is shown`() {
        every { socialDataService.postsWithComments() } returns Observable.error(RuntimeException())

        createActivity().setup()

        val postsGrid = activityController.get().findViewById<RecyclerView>(R.id.posts_grid)
        val postDetails = activityController.get().findViewById<View>(R.id.post_details)
        val toolbar = activityController.get().findViewById<View>(R.id.toolbar)
        val placeholder = activityController.get().findViewById<View>(R.id.placeholder)
        val scrim = activityController.get().findViewById<View>(R.id.scrim)

        val button = activityController.get().findViewById<TextView>(R.id.button)
        val message = activityController.get().findViewById<TextView>(R.id.placeholder_text)
        val progress = activityController.get().findViewById<View>(R.id.progress_spinner)

        assertEquals(View.GONE, postsGrid.visibility)
        assertEquals(View.GONE, postDetails.visibility)
        assertEquals(View.GONE, toolbar.visibility)
        assertEquals(View.VISIBLE, placeholder.visibility)
        assertEquals(View.VISIBLE, button.visibility)
        assertEquals(View.VISIBLE, message.visibility)
        assertEquals(View.GONE, progress.visibility)
        assertEquals(View.GONE, scrim.visibility)

        assertEquals("retry", button.text)
        assertEquals("Unable to retrieve data", message.text)
    }

    @Test
    fun `on error click to retry re-triggers call to fetch data`() {
        every { socialDataService.postsWithComments() } returns Observable.error(RuntimeException())

        createActivity().setup()

        verify(exactly = 1) { socialDataService.postsWithComments() }

        val button = activityController.get().findViewById<TextView>(R.id.button)
        button.performClick()

        verify(exactly = 2) { socialDataService.postsWithComments() }
    }

    @Test
    fun `when empty data fetched placeholder with message`() {
        every { socialDataService.postsWithComments() } returns Observable.just(emptyList())

        createActivity().setup()

        val postsGrid = activityController.get().findViewById<RecyclerView>(R.id.posts_grid)
        val postDetails = activityController.get().findViewById<View>(R.id.post_details)
        val toolbar = activityController.get().findViewById<View>(R.id.toolbar)
        val placeholder = activityController.get().findViewById<View>(R.id.placeholder)
        val scrim = activityController.get().findViewById<View>(R.id.scrim)

        val button = activityController.get().findViewById<TextView>(R.id.button)
        val message = activityController.get().findViewById<TextView>(R.id.placeholder_text)
        val progress = activityController.get().findViewById<View>(R.id.progress_spinner)

        assertEquals(View.GONE, postsGrid.visibility)
        assertEquals(View.GONE, postDetails.visibility)
        assertEquals(View.GONE, toolbar.visibility)
        assertEquals(View.VISIBLE, placeholder.visibility)
        assertEquals(View.GONE, button.visibility)
        assertEquals(View.VISIBLE, message.visibility)
        assertEquals(View.GONE, progress.visibility)
        assertEquals(View.GONE, scrim.visibility)

        assertEquals("There is no data to display", message.text)
    }

    @Test
    fun `when waiting to fetch data loading screen is shown`() {
        every { socialDataService.postsWithComments() } returns Observable.interval(3, TimeUnit.SECONDS)
            .map { Social.derivedPostsWithComments }

        createActivity().setup()

        val postsGrid = activityController.get().findViewById<RecyclerView>(R.id.posts_grid)
        val postDetails = activityController.get().findViewById<View>(R.id.post_details)
        val toolbar = activityController.get().findViewById<View>(R.id.toolbar)
        val placeholder = activityController.get().findViewById<View>(R.id.placeholder)
        val scrim = activityController.get().findViewById<View>(R.id.scrim)

        val button = activityController.get().findViewById<TextView>(R.id.button)
        val message = activityController.get().findViewById<TextView>(R.id.placeholder_text)
        val progress = activityController.get().findViewById<View>(R.id.progress_spinner)

        assertEquals(View.GONE, postsGrid.visibility)
        assertEquals(View.GONE, postDetails.visibility)
        assertEquals(View.GONE, toolbar.visibility)
        assertEquals(View.VISIBLE, placeholder.visibility)
        assertEquals(View.GONE, button.visibility)
        assertEquals(View.VISIBLE, message.visibility)
        assertEquals(View.VISIBLE, progress.visibility)
        assertEquals(View.GONE, scrim.visibility)

        assertEquals("Fetching data...", message.text)
    }

    @Test
    fun `refresh data on resume but keep grid view and do not show loading screen if already have data`() {
        createActivityAndProvideData()

        every { socialDataService.postsWithComments() } returns Observable.interval(3, TimeUnit.SECONDS)
            .map { Social.derivedPostsWithComments }

        activityController.pause()
        activityController.resume()

        val postsGrid = activityController.get().findViewById<RecyclerView>(R.id.posts_grid)
        val placeholder = activityController.get().findViewById<View>(R.id.placeholder)

        verify(exactly = 2) { socialDataService.postsWithComments() }

        assertEquals(View.VISIBLE, postsGrid.visibility)
        assertEquals(View.GONE, placeholder.visibility)
    }

    @Test
    fun `when fetch data throws error attempt to fetch again on click to retry`() {
        every { socialDataService.postsWithComments() } returns Observable.error(RuntimeException())

        createActivity().setup()

        val button = activityController.get().findViewById<TextView>(R.id.button)

        verify(exactly = 1) { socialDataService.postsWithComments() }

        button.performClick()

        verify(exactly = 2) { socialDataService.postsWithComments() }
    }

    @Test
    fun `when click on post grid item post details fragment is shown with toolbar title`() {
        createActivityAndProvideData()

        val postsGrid = activityController.get().findViewById<RecyclerView>(R.id.posts_grid)
        val toolbarTitle = activityController.get().findViewById<TextView>(R.id.toolbar_title)

        val firstPostView = postsGrid.getChildAt(0)
        firstPostView.performClick()

        assertTrue(activityController.get().supportFragmentManager.findFragmentById(R.id.post_details)!!.isVisible)
        assertEquals(View.VISIBLE, toolbarTitle.visibility)
        assertEquals("Posted by Bob", toolbarTitle.text)
    }

    @Test
    fun `when data fetched and user clicks back activity closes`() {
        createActivityAndProvideData()

        activityController.get().onBackPressed()

        assertTrue(activityController.get().isFinishing)
    }

    @Test
    fun `when post details fragment is shown and user clicks back fragment is removed and posts grid is shown without update`() {
        createActivityProvideDataAndClickFirstItem()

        assertTrue(activityController.get().supportFragmentManager.findFragmentById(R.id.post_details)!!.isVisible)

        activityController.get().onBackPressed()

        val postsGrid = activityController.get().findViewById<RecyclerView>(R.id.posts_grid)

        assertEquals(null, activityController.get().supportFragmentManager.findFragmentById(R.id.post_details))
        assertEquals(View.VISIBLE, postsGrid.visibility)
        verify(exactly = 0) { anyConstructed<PostsAdapter>().update(any()) }
    }

    @Test
    fun `when post details fragment is shown and user clicks on background scrim fragment is removed`() {
        createActivityProvideDataAndClickFirstItem()

        assertTrue(activityController.get().supportFragmentManager.findFragmentById(R.id.post_details)!!.isVisible)

        val scrim = activityController.get().findViewById<View>(R.id.scrim)
        scrim.performClick()

        assertEquals(null, activityController.get().supportFragmentManager.findFragmentById(R.id.post_details))
    }

}