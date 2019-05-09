package hmac.play.models

import hmac.play.models.raw.Comment
import hmac.play.models.raw.Post

data class PostWithComments(val author: String?, val post: Post, val comments: List<Comment>)