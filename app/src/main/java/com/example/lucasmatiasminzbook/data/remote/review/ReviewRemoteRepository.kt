package com.example.lucasmatiasminzbook.data.remote.review

import com.example.lucasmatiasminzbook.data.remote.RetrofitClient
import com.example.lucasmatiasminzbook.data.remote.api.CreateReviewRequestDto
import com.example.lucasmatiasminzbook.data.remote.api.ReviewResponseDto

class ReviewRemoteRepository {

    private val api = RetrofitClient.reviewApi

    suspend fun createReview(
        userId: Long,
        bookId: Long,
        rating: Int,
        comment: String
    ): ReviewResponseDto {
        val request = CreateReviewRequestDto(
            bookId = bookId,
            userId = userId,
            rating = rating,
            comment = comment
        )
        return api.createReview(request)
    }

    suspend fun getReviewsForBook(bookId: Long): List<ReviewResponseDto> {
        return api.getReviewsByBook(bookId)
    }
}
