/*
 * Copyright (c) 2009-2019 digi.me Limited. All rights reserved.
 */

package hmac.play

import android.app.Application
import android.support.annotation.VisibleForTesting
import hmac.play.injection.ApplicationComponent
import hmac.play.injection.DaggerApplicationComponent


open class MyApplication : Application() {
    companion object {
        lateinit var instance: MyApplication
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    lateinit var component: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        instance = this
        component = buildComponent()
    }

    private fun buildComponent(): ApplicationComponent {
        return DaggerApplicationComponent.builder()
            .application(this)
            .build()
    }
}
