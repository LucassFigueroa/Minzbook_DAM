package com.example.lucasmatiasminzbook.ui.review

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.lucasmatiasminzbook.data.remote.api.ReviewApi
import com.example.lucasmatiasminzbook.data.remote.api.ReviewResponseDto
import com.example.lucasmatiasminzbook.ui.ratings.RatingsViewModel
import com.example.lucasmatiasminzbook.util.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ReviewViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var ratingsViewModel: RatingsViewModel
    private val reviewApi: ReviewApi = mockk()

    @Before
    fun onBefore() {
        ratingsViewModel = RatingsViewModel(reviewApi)
    }

    @Test
    fun `when user ratings are loaded, the ui state is updated`() = runTest {
        // Given
        val userId = 1L
        val fakeReviews = listOf(ReviewResponseDto(1, 1, 1, 5, "Great book!", true, "", ""))
        coEvery { reviewApi.getReviewsByUser(userId) } returns fakeReviews

        // When
        ratingsViewModel.loadUserRatings(userId)

        // Then
        val uiState = ratingsViewModel.uiState.value
        assert(!uiState.isLoading)
        assert(uiState.errorMessage == null)
        Assert.assertEquals(1, uiState.reviews.size)
        Assert.assertEquals("Great book!", uiState.reviews.first().comment)
    }

    @Test
    fun `when loading ratings fails, error is updated`() = runTest {
        // Given
        val userId = 1L
        val errorMessage = "Network error"
        coEvery { reviewApi.getReviewsByUser(userId) } throws Exception(errorMessage)

        // When
        ratingsViewModel.loadUserRatings(userId)

        // Then
        val uiState = ratingsViewModel.uiState.value
        assert(!uiState.isLoading)
        Assert.assertEquals("No se pudieron cargar tus reseñas", uiState.errorMessage)
    }
}