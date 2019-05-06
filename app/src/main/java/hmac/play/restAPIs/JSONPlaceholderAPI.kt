package hmac.play.restAPIs

import hmac.play.models.raw.Comment
import hmac.play.models.raw.Post
import hmac.play.models.raw.User
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
