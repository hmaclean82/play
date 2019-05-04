package hmac.play.networking

import hmac.play.models.Comment
import hmac.play.models.Post
import hmac.play.models.User
import retrofit2.http.GET
import rx.Observable

interface JSONPlaceholderAPI {

    @GET("users")
    fun users(): Observable<List<User>>

    @GET("posts")
    fun posts(): Observable<List<Post>>

    @GET("comments")
    fun comments(): Observable<List<Comment>>

}
