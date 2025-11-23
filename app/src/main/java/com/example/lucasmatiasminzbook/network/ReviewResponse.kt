package com.example.lucasmatiasminzbook.network

data class ReviewResponseDto(
    val id: Long,
    val bookId: Long,
    val userId: Long,
    val rating: Int,
    val comment: String,
    val fechaCreacion: String
)
