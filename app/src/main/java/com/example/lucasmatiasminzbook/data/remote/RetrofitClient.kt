package com.example.lucasmatiasminzbook.data.remote

import com.example.lucasmatiasminzbook.data.remote.api.AuthApi
import com.example.lucasmatiasminzbook.data.remote.api.CatalogApi
import com.example.lucasmatiasminzbook.data.remote.api.ReviewApi
import com.example.lucasmatiasminzbook.data.remote.api.SupportApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // ================================
    // BASE URLs (microservicios)
    // ================================
    private const val BASE_URL_CATALOG = "http://10.0.2.2:8082/"
    private const val BASE_URL_AUTH = "http://10.0.2.2:8081/"
    private const val BASE_URL_REVIEW = "http://10.0.2.2:8085/"
    private const val BASE_URL_SUPPORT = "http://10.0.2.2:8084/"

    // ================================
    // HTTP CLIENT (con logs + timeouts)
    // ================================
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    // ================================
    // RETROFITS
    // ================================
    private val retrofitCatalog: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_CATALOG)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val retrofitAuth: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_AUTH)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val retrofitReview: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_REVIEW)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val retrofitSupport: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_SUPPORT)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ================================
    // APIs listas para usar
    // ================================
    val catalogApi: CatalogApi by lazy {
        retrofitCatalog.create(CatalogApi::class.java)
    }

    val authApi: AuthApi by lazy {
        retrofitAuth.create(AuthApi::class.java)
    }

    val reviewApi: ReviewApi by lazy {
        retrofitReview.create(ReviewApi::class.java)
    }

    val supportApi: SupportApi by lazy {
        retrofitSupport.create(SupportApi::class.java)
    }
}
