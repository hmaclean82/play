package hmac.play.restAPIs

import hmac.play.models.PostWithUsername
import hmac.play.models.raw.Comment
import hmac.play.models.raw.Post
import hmac.play.models.raw.User
import rx.Observable

interface SocialDataService {
    fun users(): Observable<List<User>>
    fun posts(): Observable<List<Post>>
    fun comments(): Observable<List<Comment>>
    fun postsWithUsernames(): Observable<List<PostWithUsername>>
}

class SocialDataServiceImpl (private val socialDataAPI: JSONPlaceholderAPI): SocialDataService {

    override fun users() = socialDataAPI.users()

    override fun posts() = socialDataAPI.posts()

    override fun comments() = socialDataAPI.comments()

    override fun postsWithUsernames(): Observable<List<PostWithUsername>> {
        val postsObs = posts()
        val usersObs = users()
            .onErrorReturn { emptyList() }

        return Observable.zip<List<Post>, List<User>, List<PostWithUsername>>(postsObs, usersObs) { posts, users ->
            posts.map { post ->
                val username = users.find { it.id == post.userId }?.takeIf { it.id != null }?.username
                PostWithUsername(username, post) }
        }
    }

}

