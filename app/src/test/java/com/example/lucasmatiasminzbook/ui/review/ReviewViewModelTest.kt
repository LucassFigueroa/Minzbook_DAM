package com.example.lucasmatiasminzbook.ui.review

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.lucasmatiasminzbook.data.remote.api.ReviewApi
import com.example.lucasmatiasminzbook.data.remote.api.ReviewResponseDto
import com.example.lucasmatiasminzbook.ui.ratings.RatingsViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ReviewViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var ratingsViewModel: RatingsViewModel
    private val reviewApi: ReviewApi = mockk()

    @Before
    fun onBefore() {
        Dispatchers.setMain(testDispatcher)
        ratingsViewModel = RatingsViewModel(reviewApi)
    }

    @After
    fun onAfter() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when user ratings are loaded, the ui state is updated`() = runTest {
        // Given
        val userId = 1L
        val fakeReviews = listOf(ReviewResponseDto(1, 1, 1, 5, "Great book!", true, "", ""))
        coEvery { reviewApi.getReviewsByUser(userId) } returns fakeReviews

        // When
        ratingsViewModel.loadUserRatings(userId)
        advanceUntilIdle()

        // Then
        val uiState = ratingsViewModel.uiState.value
        Assert.assertFalse(uiState.isLoading)
        Assert.assertNull(uiState.errorMessage)
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
        advanceUntilIdle()

        // Then
        val uiState = ratingsViewModel.uiState.value
        Assert.assertFalse(uiState.isLoading)
        Assert.assertEquals("No se pudieron cargar tus reseñas", uiState.errorMessage)
    }
}
