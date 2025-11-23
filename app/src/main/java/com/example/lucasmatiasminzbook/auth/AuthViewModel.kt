package com.example.lucasmatiasminzbook.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lucasmatiasminzbook.data.remote.auth.AuthRepository
import com.example.lucasmatiasminzbook.store.AuthLocalStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val userName: String? = null,
    val email: String? = null,
    val role: String? = null,
    val token: String? = null
)

class AuthViewModel(
    private val authRepo: AuthRepository,
    private val store: AuthLocalStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        loadSession()
    }

    // ============================================================
    //      LOGIN REAL
    // ============================================================
    fun login(email: String, password: String, onError: (String?) -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = authRepo.login(email, password)

            if (result.isFailure) {
                onError(result.exceptionOrNull()?.localizedMessage ?: "Error desconocido")
                return@launch
            }

            val user = result.getOrNull()!!

            store.saveSession(
                email = user.email,
                name = user.nombre,
                role = user.rol,
                token = user.token
            )

            _uiState.value = AuthUiState(
                isAuthenticated = true,
                userName = user.nombre,
                email = user.email,
                role = user.rol,
                token = user.token
            )

            onSuccess()
        }
    }

    // ============================================================
    //       CARGAR SESIÓN AUTOMÁTICA
    // ============================================================
    private fun loadSession() {
        viewModelScope.launch {
            val session = store.loadSession()
            if (session != null) {
                _uiState.value = AuthUiState(
                    isAuthenticated = true,
                    userName = session.name,
                    email = session.email,
                    role = session.role,
                    token = session.token
                )
            }
        }
    }

    // ============================================================
    //       LOGOUT REAL
    // ============================================================
    fun logout() {
        viewModelScope.launch {
            store.clear()
            _uiState.value = AuthUiState()
        }
    }
}
