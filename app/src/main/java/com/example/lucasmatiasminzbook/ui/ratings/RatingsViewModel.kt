package com.example.lucasmatiasminzbook.ui.ratings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lucasmatiasminzbook.data.remote.RetrofitClient
import com.example.lucasmatiasminzbook.data.remote.api.ReviewApi
import com.example.lucasmatiasminzbook.data.remote.api.ReviewResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RatingsViewModel(
    private val api: ReviewApi = RetrofitClient.reviewApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(RatingsUiState())
    val uiState: StateFlow<RatingsUiState> = _uiState

    fun loadUserRatings(userId: Long) {
        _uiState.value = RatingsUiState(isLoading = true)

        viewModelScope.launch {
            try {
                val dtos: List<ReviewResponseDto> = api.getReviewsByUser(userId)

                val mapped: List<UserReviewUi> = dtos.map { dto: ReviewResponseDto ->
                    UserReviewUi(
                        id = dto.id,
                        bookId = dto.bookId,
                        rating = dto.rating,
                        comment = dto.comment,
                        fecha = dto.fechaCreacion ?: ""
                    )
                }

                _uiState.value = RatingsUiState(
                    isLoading = false,
                    reviews = mapped,
                    errorMessage = null
                )
            } catch (e: Exception) {
                Log.e("RatingsViewModel", "Error al cargar reseñas", e)
                _uiState.value = RatingsUiState(
                    isLoading = false,
                    reviews = emptyList(),
                    errorMessage = "No se pudieron cargar tus reseñas"
                )
            }
        }
    }
}
