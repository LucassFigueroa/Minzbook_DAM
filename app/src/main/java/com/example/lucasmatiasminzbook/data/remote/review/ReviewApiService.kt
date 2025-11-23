package com.example.lucasmatiasminzbook.data.remote.review

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewApiService {

    @GET("reviews/book/{bookId}")
    suspend fun getReviewsByBook(
        @Path("bookId") bookId: Long
    ): List<ReviewDto>

    @POST("reviews")
    suspend fun createReview(
        @Body request: CreateReviewRequest
    ): ReviewDto
    @GET("reviews/user/{userId}")
    suspend fun getReviewsByUser(@Path("userId") userId: Long): List<ReviewResponseDto>
}

