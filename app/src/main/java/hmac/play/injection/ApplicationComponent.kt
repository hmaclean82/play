package hmac.play.injection

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * Application component, which provides dependencies into the application
 */

@Singleton
@Component(modules = arrayOf(NetworkingModule::class))
interface ApplicationComponent: ComponentGraph {

    @Component.Builder
    interface Builder {
        fun build(): ApplicationComponent

        @BindsInstance
        fun application(@ForApplication appContext: Context): Builder
    }

}