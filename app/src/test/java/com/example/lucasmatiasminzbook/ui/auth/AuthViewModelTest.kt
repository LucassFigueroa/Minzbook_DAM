package com.example.lucasmatiasminzbook.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.lucasmatiasminzbook.data.remote.auth.AuthRepository
import com.example.lucasmatiasminzbook.data.remote.dto.UserResponse
import com.example.lucasmatiasminzbook.viewmodel.AuthViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
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
class AuthViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @RelaxedMockK
    private lateinit var authRepository: AuthRepository

    private lateinit var authViewModel: AuthViewModel

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        authViewModel = AuthViewModel(authRepository)
    }

    @After
    fun onAfter() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() {
        val uiState = authViewModel.uiState.value
        Assert.assertFalse(uiState.isAuthenticated)
        Assert.assertNull(uiState.userName)
        Assert.assertNull(uiState.email)
        Assert.assertNull(uiState.role)
        Assert.assertNull(uiState.userId)
        Assert.assertFalse(uiState.isLoading)
        Assert.assertNull(uiState.error)
    }

    @Test
    fun `when login fails, error is updated`() = runTest {
        // Given
        val errorMessage = "Invalid credentials"
        coEvery { authRepository.login(any(), any()) } throws Exception(errorMessage)

        // When
        authViewModel.login("test@test.com", "password")
        advanceUntilIdle()

        // Then
        val uiState = authViewModel.uiState.value
        Assert.assertFalse(uiState.isAuthenticated)
        Assert.assertEquals(errorMessage, uiState.error)
    }

    @Test
    fun `when register is successful, state is updated`() = runTest {
        // Given
        val userResponse = UserResponse(1, "Test", "User", "test@test.com", "USER")
        coEvery { authRepository.register(any(), any(), any(), any()) } returns userResponse

        // When
        authViewModel.register("Test User", "test@test.com", "password")
        advanceUntilIdle()

        // Then
        val uiState = authViewModel.uiState.value
        Assert.assertNull(uiState.error)
    }

    @Test
    fun `when register fails, error is updated`() = runTest {
        // Given
        val errorMessage = "Email already exists"
        coEvery { authRepository.register(any(), any(), any(), any()) } throws Exception(errorMessage)

        // When
        authViewModel.register("Test User", "test@test.com", "password")
        advanceUntilIdle()

        // Then
        val uiState = authViewModel.uiState.value
        Assert.assertFalse(uiState.isAuthenticated)
        Assert.assertEquals(errorMessage, uiState.error)
    }
}
