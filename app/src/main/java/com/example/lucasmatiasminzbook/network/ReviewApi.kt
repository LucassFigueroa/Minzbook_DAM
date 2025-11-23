package com.example.lucasmatiasminzbook.network

import retrofit2.http.GET
import retrofit2.http.Path

interface ReviewApi {

    @GET("reviews/user/{userId}")
    suspend fun getReviewsByUser(@Path("userId") userId: Long): List<ReviewResponseDto>
}
