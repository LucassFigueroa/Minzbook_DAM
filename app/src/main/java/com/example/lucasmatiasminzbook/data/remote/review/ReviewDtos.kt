package com.example.lucasmatiasminzbook.data.remote.review

data class ReviewDto(
    val id: Long,
    val bookId: Long,
    val userId: Long,
    val rating: Int,
    val comment: String,
    val createdAt: String
)

data class CreateReviewRequest(
    val bookId: Long,
    val userId: Long,
    val rating: Int,
    val comment: String
)
