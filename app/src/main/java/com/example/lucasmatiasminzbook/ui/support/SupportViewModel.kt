package com.example.lucasmatiasminzbook.ui.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lucasmatiasminzbook.data.local.ticket.MessageAuthor
import com.example.lucasmatiasminzbook.data.local.ticket.Ticket
import com.example.lucasmatiasminzbook.data.local.ticket.TicketMessage
import com.example.lucasmatiasminzbook.data.local.ticket.TicketRepository
import com.example.lucasmatiasminzbook.data.local.user.Role
import com.example.lucasmatiasminzbook.data.local.user.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SupportUiState(
    val currentUser: UserEntity? = null,
    val tickets: List<Ticket> = emptyList(),
    val messages: List<TicketMessage> = emptyList(),
    val isLoading: Boolean = true,
    val selectedTicketId: Long? = null
)

class SupportViewModel(private val ticketRepository: TicketRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SupportUiState())
    val uiState: StateFlow<SupportUiState> = _uiState.asStateFlow()

    fun setCurrentUser(user: UserEntity) {
        _uiState.value = _uiState.value.copy(currentUser = user)
        loadTickets()
    }

    private fun loadTickets() {
        viewModelScope.launch {
            val user = _uiState.value.currentUser ?: return@launch
            val ticketsFlow = when (user.role) {
                Role.SUPPORT -> ticketRepository.getAllTickets()
                else -> ticketRepository.getTicketsByUser(user.id)
            }

            ticketsFlow.collect { tickets ->
                _uiState.value = _uiState.value.copy(tickets = tickets, isLoading = false)
            }
        }
    }

    fun selectTicket(ticketId: Long) {
        _uiState.value = _uiState.value.copy(selectedTicketId = ticketId)
        viewModelScope.launch {
            ticketRepository.getMessagesForTicket(ticketId).collect { messages ->
                _uiState.value = _uiState.value.copy(messages = messages)
            }
        }
    }

    fun addTicket(subject: String, firstMessage: String) {
        viewModelScope.launch {
            val userId = _uiState.value.currentUser?.id ?: return@launch
            val ticket = Ticket(userId = userId, subject = subject)
            val message = TicketMessage(ticketId = 0, author = MessageAuthor.USER, message = firstMessage)
            ticketRepository.addTicket(ticket, message)
        }
    }

    fun addMessage(messageText: String) {
        viewModelScope.launch {
            val ticketId = _uiState.value.selectedTicketId ?: return@launch
            val author = if (_uiState.value.currentUser?.role == Role.SUPPORT) MessageAuthor.SUPPORT else MessageAuthor.USER
            val message = TicketMessage(ticketId = ticketId, author = author, message = messageText)
            ticketRepository.addMessage(message)
        }
    }

    fun resolveTicket(ticketId: Long, isResolved: Boolean) {
        viewModelScope.launch {
            ticketRepository.setResolved(ticketId, isResolved)
        }
    }
}
