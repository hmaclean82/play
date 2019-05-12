package hmac.play

import com.google.gson.Gson
import hmac.play.data.Social
import hmac.play.models.PostWithComments
import hmac.play.restAPIs.JSONPlaceholderAPI
import hmac.play.restAPIs.SocialDataServiceImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.observers.TestSubscriber

class SocialDataServiceImplTest {

    @MockK
    private lateinit var jsonPlaceholderAPI: JSONPlaceholderAPI

    private lateinit var socialDataServiceImpl: SocialDataServiceImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        socialDataServiceImpl = SocialDataServiceImpl(jsonPlaceholderAPI)

        every { jsonPlaceholderAPI.posts() } returns Observable.just(Social.posts)
        every { jsonPlaceholderAPI.users() } returns Observable.just(Social.users)
        every { jsonPlaceholderAPI.comments() } returns Observable.just(Social.comments)
    }

    @Test
    fun `postsWithComments combines data from raw api responses`() {
        val testSubscriber = TestSubscriber.create<List<PostWithComments>>()
        val subscription = socialDataServiceImpl.postsWithComments().subscribe(testSubscriber)

        testSubscriber.assertValue(Social.derivedPostsWithComments)
        subscription.unsubscribe()
    }


}