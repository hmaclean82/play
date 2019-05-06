package hmac.play.injection

import hmac.play.screens.PostsActivity

interface ComponentGraph {

    fun inject(postActivity: PostsActivity)

}