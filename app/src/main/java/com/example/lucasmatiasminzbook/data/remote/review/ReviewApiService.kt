package com.example.lucasmatiasminzbook.data.remote.review

import com.example.lucasmatiasminzbook.data.remote.api.CreateReviewRequestDto
import com.example.lucasmatiasminzbook.data.remote.api.ReviewResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewApiService {

    @GET("reviews/book/{bookId}")
    suspend fun getReviewsByBook(
        @Path("bookId") bookId: Long
    ): List<ReviewResponseDto>

    @POST("reviews")
    suspend fun createReview(
        @Body request: CreateReviewRequestDto
    ): ReviewResponseDto
    
    @GET("reviews/user/{userId}")
    suspend fun getReviewsByUser(@Path("userId") userId: Long): List<ReviewResponseDto>
}
