package com.example.lucasmatiasminzbook.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.lucasmatiasminzbook.data.remote.auth.AuthRepository
import com.example.lucasmatiasminzbook.util.MainCoroutineRule
import com.example.lucasmatiasminzbook.viewmodel.AuthViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @RelaxedMockK
    private lateinit var authRepository: AuthRepository

    private lateinit var authViewModel: AuthViewModel

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        authViewModel = AuthViewModel(authRepository)
    }

    @Test
    fun `initial state is correct`() {
        val uiState = authViewModel.uiState.value
        assert(!uiState.isAuthenticated)
        assert(uiState.userName == null)
        assert(uiState.email == null)
        assert(uiState.role == null)
        assert(uiState.userId == null)
        assert(!uiState.isLoading)
        assert(uiState.error == null)
    }

    @Test
    fun `when login fails, error is updated`() = runTest {
        // Given
        val errorMessage = "Invalid credentials"
        coEvery { authRepository.login(any(), any()) } throws Exception(errorMessage)

        // When
        authViewModel.login("test@test.com", "password")

        // Then
        val uiState = authViewModel.uiState.value
        assert(!uiState.isAuthenticated)
        Assert.assertEquals(errorMessage, uiState.error)
    }
}