package com.example.lucasmatiasminzbook.ui.ratings

data class UserReviewUi(
    val id: Long,
    val bookId: Long,
    val rating: Int,
    val comment: String,
    val fecha: String
)

data class RatingsUiState(
    val isLoading: Boolean = false,
    val reviews: List<UserReviewUi> = emptyList(),
    val errorMessage: String? = null
)
