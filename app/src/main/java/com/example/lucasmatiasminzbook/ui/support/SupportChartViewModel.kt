package com.example.lucasmatiasminzbook.ui.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lucasmatiasminzbook.data.remote.repository.SupportRepository
import com.example.lucasmatiasminzbook.data.remote.support.SupportMessageDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SupportChatUiState(
    val isLoading: Boolean = false,
    val messages: List<SupportMessageDto> = emptyList(),
    val sending: Boolean = false,
    val closing: Boolean = false,
    val ticketClosed: Boolean = false,   // 👈 NUEVO: para avisar que se cerró
    val error: String? = null
)

class SupportChatViewModel(
    private val repository: SupportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SupportChatUiState())
    val uiState: StateFlow<SupportChatUiState> = _uiState

    fun loadMessages(conversationId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                val msgs = repository.getMessages(conversationId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    messages = msgs
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error cargando mensajes"
                )
            }
        }
    }

    fun sendMessage(conversationId: Long, userId: Long, contenido: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(sending = true, error = null)
                repository.sendMessage(conversationId, userId, contenido)
                // recargar mensajes después de enviar
                val msgs = repository.getMessages(conversationId)
                _uiState.value = _uiState.value.copy(
                    sending = false,
                    messages = msgs
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    sending = false,
                    error = e.message ?: "Error enviando mensaje"
                )
            }
        }
    }

    fun closeConversation(conversationId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(closing = true, error = null)
                repository.closeConversation(conversationId)
                _uiState.value = _uiState.value.copy(
                    closing = false,
                    ticketClosed = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    closing = false,
                    error = e.message ?: "Error al cerrar el ticket"
                )
            }
        }
    }

    fun clearTicketClosedFlag() {
        _uiState.value = _uiState.value.copy(ticketClosed = false)
    }
}
