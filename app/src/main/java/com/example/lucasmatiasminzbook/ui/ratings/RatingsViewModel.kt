package com.example.lucasmatiasminzbook.ui.ratings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ReviewUiModel(
    val id: Long,
    val rating: Int,
    val comment: String
)

data class RatingsUiState(
    val isLoading: Boolean = false,
    val reviews: List<ReviewUiModel> = emptyList(),
    val reviewSent: Boolean = false,
    val error: String? = null
)

class RatingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RatingsUiState())
    val uiState: StateFlow<RatingsUiState> = _uiState

    // Aquí después puedes conectar con tu microservicio de reviews
    fun createReview(
        userId: Long,
        rating: Int,
        comment: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

                // Simulación de llamada remota
                delay(500)

                val newReview = ReviewUiModel(
                    id = System.currentTimeMillis(),
                    rating = rating,
                    comment = comment
                )

                val updatedList = _uiState.value.reviews + newReview

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    reviews = updatedList,
                    reviewSent = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al enviar la reseña"
                )
            }
        }
    }

    fun clearReviewSentFlag() {
        _uiState.value = _uiState.value.copy(reviewSent = false)
    }
}
