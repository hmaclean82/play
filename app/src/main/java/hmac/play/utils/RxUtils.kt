package hmac.play.utils

import rx.subscriptions.CompositeSubscription

object RxUtils {

    fun getNewCompositeSubIfUnsubscribed(subscription: CompositeSubscription?): CompositeSubscription {
        return subscription?.takeUnless { it.isUnsubscribed }
            ?: CompositeSubscription()
    }
}