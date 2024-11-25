package com.hallyu.style.networkservice

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.hallyu.style.HallyuApplication
import com.hallyu.style.data.UserManager
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitService {
    var retrofit: Retrofit? = null
    fun buildRetrofit(context: Context): ApiService {
        if (httpClient.interceptors().isNotEmpty()) {
            httpClient.interceptors().clear()
        }
        val authTokenProvider = UserManager.getInstance(context)

        httpClient.readTimeout(60, TimeUnit.SECONDS)
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
        httpClient.addInterceptor(setLogger())
        httpClient.addInterceptor(Interceptor { chain: Interceptor.Chain ->
            val original = chain.request()
            val requestBuilder: Request.Builder = original.newBuilder()
            if (authTokenProvider.isLogged()){
                requestBuilder.header(
                    "Authorization",
                    "Bearer ${authTokenProvider.getAccessToken()}"
                )
                Log.i("TAG", "buildRetrofit: Authorization : Bearer token::: $${authTokenProvider.getAccessToken()}")

            }
            requestBuilder.header("Accept", "application/json")
            requestBuilder.header("Language", "${HallyuApplication.lang}")
            Log.i("TAG", "buildRetrofit: Accept : application/json, Language : ${HallyuApplication.lang}")
            requestBuilder.method(original.method, original.body)
            val request: Request = requestBuilder.build()
            chain.proceed(request)
        })
        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(httpClient.build())
            .build()
        return retrofit!!.create<ApiService>(ApiService::class.java)
    }
//
//    fun getRequest(): Observable<Response<JsonArray?>?>? {
//
//        return service.homeIndex()
//    }

    companion object {
        //const val SERVER_URL = "https://hallyustyle.com/api/store-front/"
        const val SERVER_URL = "http://localhost/hallyustyle.com/api/store-front/"
        private val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
        fun setLogger(): HttpLoggingInterceptor {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            return logging
        }

        fun isConnected(context: Context): Boolean {
            val connectivityManager: ConnectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo: NetworkInfo? = connectivityManager.getActiveNetworkInfo()
            return activeNetworkInfo != null && activeNetworkInfo.isConnected()
        }
    }
}