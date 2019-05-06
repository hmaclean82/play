/*
 * Copyright (c) 2009-2019 digi.me Limited. All rights reserved.
 */

package hmac.play

import android.app.Application
import hmac.play.injection.ApplicationComponent
import hmac.play.injection.DaggerApplicationComponent
import timber.log.Timber


class MyApplication : Application() {
    companion object {
        lateinit var instance: MyApplication
    }

    lateinit var component: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        instance = this
        component = buildComponent()
    }

    fun buildComponent(): ApplicationComponent {
        return DaggerApplicationComponent.builder()
            .application(this)
            .build()
    }
}
