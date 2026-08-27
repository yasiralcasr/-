package com.example.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Security Interceptor that injects the required API Key, Secret Key, App ID,
 * and Master Bearer Token into all outgoing HTTP requests.
 */
class SecurityKeyInterceptor(
    private val apiKey: String = DEFAULT_API_KEY,
    private val secretKey: String = DEFAULT_SECRET_KEY,
    private val appId: String = DEFAULT_APP_ID,
    private val masterToken: String = DEFAULT_MASTER_TOKEN
) : Interceptor {

    companion object {
        const val DEFAULT_API_KEY = "mLj1RiAns8sPbsgJrHmsziDUdsoGmDJf"
        const val DEFAULT_SECRET_KEY = "CPF5SxTJbDc3aq9q"
        const val DEFAULT_APP_ID = "Trial_App_35278"
        const val DEFAULT_MASTER_TOKEN = "1073781088@0503026675#8054\$8051%"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val authenticatedRequest = original.newBuilder()
            .header("X-API-KEY", apiKey)
            .header("X-API-SECRET", secretKey)
            .header("X-APP-ID", appId)
            .header("Authorization", "Bearer $masterToken")
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .header("X-Client-Platform", "Android-EastWestGlobal-Enterprise")
            .method(original.method, original.body)
            .build()

        return chain.proceed(authenticatedRequest)
    }
}

/**
 * Singleton Retrofit Client Provider for External Platform APIs
 */
object RetrofitClient {

    private const val BASE_URL = "https://api.wathq.sa/"

    val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        OkHttpClient.Builder()
            .addInterceptor(SecurityKeyInterceptor())
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val apiService: ExternalPlatformApiService by lazy {
        retrofit.create(ExternalPlatformApiService::class.java)
    }
}
