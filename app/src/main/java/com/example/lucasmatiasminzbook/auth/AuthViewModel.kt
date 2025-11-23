package com.example.lucasmatiasminzbook.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lucasmatiasminzbook.data.remote.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val displayName: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    // ==========================
    // LOGIN REAL (si quisieras usarlo desde la UI)
    // ==========================
    fun login(email: String, password: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val response = repository.login(email, password)

                _uiState.value = AuthUiState(
                    isAuthenticated = true,
                    displayName = "${response.nombre} ${response.apellido}",
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    isAuthenticated = false,
                    displayName = null,
                    isLoading = false,
                    error = e.message ?: "Error al iniciar sesión"
                )
            }
        }
    }

    // ==========================
    // REGISTER REAL (si algún día lo llamas desde aquí)
    // ==========================
    fun register(nombre: String, apellido: String, email: String, password: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val response = repository.register(nombre, apellido, email, password)

                _uiState.value = AuthUiState(
                    isAuthenticated = true,
                    displayName = "${response.nombre} ${response.apellido}",
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    isAuthenticated = false,
                    displayName = null,
                    isLoading = false,
                    error = e.message ?: "Error al registrarse"
                )
            }
        }
    }

    // ==========================
    // SIMULATE LOGIN (lo usa MainActivity)
    // ==========================
    fun simulateLogin(displayName: String = "Usuario") {
        _uiState.value = AuthUiState(
            isAuthenticated = true,
            displayName = displayName,
            isLoading = false,
            error = null
        )
    }

    // ==========================
    // SIMULATE LOGOUT (lo usa MainActivity)
    // ==========================
    fun simulateLogout() {
        _uiState.value = AuthUiState(
            isAuthenticated = false,
            displayName = null,
            isLoading = false,
            error = null
        )
    }
}
