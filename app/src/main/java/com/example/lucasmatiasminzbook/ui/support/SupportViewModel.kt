package com.example.lucasmatiasminzbook.ui.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lucasmatiasminzbook.data.remote.RetrofitClient
import com.example.lucasmatiasminzbook.data.remote.repository.SupportRepository
import com.example.lucasmatiasminzbook.data.remote.support.SupportConversationDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SupportUiState(
    val isLoading: Boolean = false,
    val tickets: List<SupportConversationDto> = emptyList(),
    val ticketCreated: Boolean = false,
    val error: String? = null
)

class SupportViewModel(
    private val repository: SupportRepository = SupportRepository(
        RetrofitClient.supportApi           // 👈 MUY IMPORTANTE
    )
) : ViewModel() {

    private val _uiState = MutableStateFlow(SupportUiState())
    val uiState: StateFlow<SupportUiState> = _uiState

    fun loadUserTickets(userId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                val conversations = repository.getUserConversations(userId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    tickets = conversations
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error cargando tickets"
                )
            }
        }
    }

    fun loadAllTickets() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                val conversations = repository.getAllConversations()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    tickets = conversations
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error cargando tickets"
                )
            }
        }
    }

    fun createTicket(
        userId: Long?,
        email: String,
        subject: String,
        message: String
    ) {
        if (userId == null) {
            _uiState.value = _uiState.value.copy(
                error = "Usuario no identificado para crear ticket"
            )
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                repository.createConversation(
                    userId = userId,
                    asunto = subject,
                    mensaje = message
                )

                val conversations = repository.getUserConversations(userId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    tickets = conversations,
                    ticketCreated = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al enviar el ticket"
                )
            }
        }
    }

    fun clearTicketCreatedFlag() {
        _uiState.value = _uiState.value.copy(ticketCreated = false)
    }
}
