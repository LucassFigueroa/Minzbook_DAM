package com.example.lucasmatiasminzbook.data.remote.repository

import com.example.lucasmatiasminzbook.data.remote.api.CreateReviewRequestDto
import com.example.lucasmatiasminzbook.data.remote.api.ReviewResponseDto
import com.example.lucasmatiasminzbook.data.remote.review.ReviewApiService

class ReviewRepository(
    private val api: ReviewApiService
) {

    suspend fun getReviewsForBook(bookId: Long): List<ReviewResponseDto> {
        return api.getReviewsByBook(bookId)
    }

    suspend fun addReview(
        bookId: Long,
        userId: Long,
        rating: Int,
        comment: String
    ): ReviewResponseDto {
        val req = CreateReviewRequestDto(
            bookId = bookId,
            userId = userId,
            rating = rating,
            comment = comment
        )
        return api.createReview(req)
    }
}
