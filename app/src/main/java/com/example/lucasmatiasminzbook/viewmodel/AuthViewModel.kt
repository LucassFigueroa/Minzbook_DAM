package com.example.lucasmatiasminzbook.viewmodel

import androidx.lifecycle.ViewModel
import com.example.lucasmatiasminzbook.data.remote.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ============================================================
// ESTADO DE AUTENTICACIÓN
// ============================================================
data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val userName: String? = null,
    val email: String? = null,
    val role: String? = null,
    val userId: Long? = null,          // 👈 NECESARIO PARA REVIEWS + SUPPORT
    val isLoading: Boolean = false,
    val error: String? = null
)

// ============================================================
// VIEWMODEL
// ============================================================
class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    // ============================================================
    // LOGIN contra microservicio AUTH
    // ============================================================
    suspend fun login(email: String, password: String): String? {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        return try {
            val user = repository.login(email, password)

            // ⬅ Aquí cargamos TODO lo que viene del microservicio
            _uiState.value = AuthUiState(
                isAuthenticated = true,
                userName = "${user.nombre} ${user.apellido}".trim(),
                email = user.email,
                role = user.rol,
                userId = user.id,           // 👈 EL ID SE GUARDA AQUÍ
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
    suspend fun register(name: String, email: String, password: String): String? {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

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
                userId = user.id,        // 👈 TAMBIÉN AQUÍ
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
    // LOGOUT → limpia todo el estado
    // ============================================================
    fun logout() {
        _uiState.value = AuthUiState()
    }
}
