package hmac.play.models

import hmac.play.models.raw.Post

data class PostWithUsername(val username: String?, val post: Post)