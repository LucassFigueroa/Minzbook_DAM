package com.example.lucasmatiasminzbook.viewmodel

import androidx.lifecycle.ViewModel
import com.example.lucasmatiasminzbook.data.remote.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val userName: String? = null,
    val email: String? = null,
    val role: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    // ============================================================
    // LOGIN contra microservicio AUTH
    // ============================================================
    /**
     * @return null si todo OK, o mensaje de error si falla
     */
    suspend fun login(email: String, password: String): String? {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        return try {
            val user = repository.login(email, password)

            _uiState.value = AuthUiState(
                isAuthenticated = true,
                userName = "${user.nombre} ${user.apellido}".trim(),
                email = user.email,
                role = user.rol,
                isLoading = false,
                error = null
            )

            null  // sin error
        } catch (e: Exception) {
            val msg = e.message ?: "Error al iniciar sesión"
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = msg
            )
            msg
        }
    }

    // ============================================================
    // REGISTER contra microservicio AUTH
    // ============================================================
    /**
     * @param name viene como "Nombre Apellido" desde la UI
     * @return null si todo OK, o mensaje de error si falla
     */
    suspend fun register(name: String, email: String, password: String): String? {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        // separamos "Nombre Apellido" en nombre y apellido
        val partes = name.trim().split(" ", limit = 2)
        val nombre = partes.getOrNull(0) ?: ""
        val apellido = partes.getOrNull(1) ?: ""

        return try {
            val user = repository.register(
                nombre = nombre,
                apellido = apellido,
                email = email,
                password = password
            )

            _uiState.value = AuthUiState(
                isAuthenticated = true,
                userName = "${user.nombre} ${user.apellido}".trim(),
                email = user.email,
                role = user.rol,
                isLoading = false,
                error = null
            )

            null
        } catch (e: Exception) {
            val msg = e.message ?: "Error al registrarse"
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = msg
            )
            msg
        }
    }

    // ============================================================
    // LOGOUT sencillo (limpia el estado)
    // ============================================================
    fun logout() {
        _uiState.value = AuthUiState()
    }
}
