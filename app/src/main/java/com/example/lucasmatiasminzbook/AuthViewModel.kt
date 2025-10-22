package com.example.lucasmatiasminzbook

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val displayName: String? = null
)

class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun simulateLogin(name: String = "Pía") {
        _uiState.value = AuthUiState(isAuthenticated = true, displayName = name)
    }

    fun simulateLogout() {
        _uiState.value = AuthUiState()
    }
}
