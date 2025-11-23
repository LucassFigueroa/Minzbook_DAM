package com.example.lucasmatiasminzbook.data.remote

import com.example.lucasmatiasminzbook.data.remote.api.AuthApi
import com.example.lucasmatiasminzbook.data.remote.api.CatalogApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // ================================
    // BASE URLs
    // ================================
    private const val BASE_URL_CATALOG = "http://10.0.2.2:8082/"
    private const val BASE_URL_AUTH = "http://10.0.2.2:8081/"  // ← cambia si tu auth usa otro puerto

    // ================================
    // HTTP CLIENT (con logs)
    // ================================
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
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

    // ================================
    // APIs
    // ================================
    val catalogApi: CatalogApi by lazy {
        retrofitCatalog.create(CatalogApi::class.java)
    }

    val authApi: AuthApi by lazy {
        retrofitAuth.create(AuthApi::class.java)
    }
}
