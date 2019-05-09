package hmac.play.restAPIs

import hmac.play.models.PostWithComments
import hmac.play.models.raw.Comment
import hmac.play.models.raw.Post
import hmac.play.models.raw.User
import rx.Observable

interface SocialDataService {
    fun users(): Observable<List<User>>
    fun posts(): Observable<List<Post>>
    fun comments(): Observable<List<Comment>>
    fun postsWithComments(): Observable<List<PostWithComments>>
}

class SocialDataServiceImpl (private val socialDataAPI: JSONPlaceholderAPI): SocialDataService {

    override fun users() = socialDataAPI.users()

    override fun posts() = socialDataAPI.posts()

    override fun comments() = socialDataAPI.comments()

    override fun postsWithComments(): Observable<List<PostWithComments>> {
        val postsObs = posts()
        val usersObs = users()
            .onErrorReturn { emptyList() }
        val commentsObs = comments()
            .onErrorReturn { emptyList() }

        return Observable.zip<List<Post>, List<User>, List<Comment>, List<PostWithComments>>(postsObs, usersObs, commentsObs) { posts, users, comments ->
            posts.map { post ->
                val authorName = users.find { it.id == post.userId && it.id != null }?.name
                val postComments = comments.filter { it.postId == post.id && it.postId != null }
                PostWithComments(authorName, post, postComments) }
        }
    }

}

