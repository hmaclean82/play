package hmac.play.restAPIs

import hmac.play.models.raw.Comment
import hmac.play.models.raw.Post
import hmac.play.models.raw.User
import rx.Observable

interface ISocialData {
    fun users(): Observable<List<User>>
    fun posts(): Observable<List<Post>>
    fun comments(): Observable<List<Comment>>
}