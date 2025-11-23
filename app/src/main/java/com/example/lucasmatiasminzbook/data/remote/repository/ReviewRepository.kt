package com.example.lucasmatiasminzbook.data.remote.repository

import com.example.lucasmatiasminzbook.data.remote.review.CreateReviewRequest
import com.example.lucasmatiasminzbook.data.remote.review.ReviewApiService
import com.example.lucasmatiasminzbook.data.remote.review.ReviewDto

class ReviewRepository(
    private val api: ReviewApiService
) {

    suspend fun getReviewsForBook(bookId: Long): List<ReviewDto> {
        return api.getReviewsByBook(bookId)
    }

    suspend fun addReview(
        bookId: Long,
        userId: Long,
        rating: Int,
        comment: String
    ): ReviewDto {
        val req = CreateReviewRequest(
            bookId = bookId,
            userId = userId,
            rating = rating,
            comment = comment
        )
        return api.createReview(req)
    }
}
