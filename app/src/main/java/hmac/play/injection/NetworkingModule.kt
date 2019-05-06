package hmac.play.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import hmac.play.R
import hmac.play.restAPIs.ISocialData
import hmac.play.restAPIs.JSONPlaceholderAPI
import hmac.play.restAPIs.SocialDataService
import hmac.play.restAPIs.SocialDataServiceImpl
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkingModule {

    @Provides
    internal fun defaultRetrofitBuilder(): Retrofit.Builder {
        val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build()

        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
    }

    @Provides
    @Singleton
    internal fun provideSocialDataService(builder: Retrofit.Builder, @ForApplication context: Context): SocialDataService {
        val baseUrl = HttpUrl.parse(context.resources.getString(R.string.json_placeholder_base_url))
        val retrofit = builder.baseUrl(baseUrl!!).build()
        return SocialDataServiceImpl(retrofit.create(JSONPlaceholderAPI::class.java))
    }

}