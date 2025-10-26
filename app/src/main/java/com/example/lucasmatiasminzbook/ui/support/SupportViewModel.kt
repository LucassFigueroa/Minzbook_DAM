package com.example.lucasmatiasminzbook.ui.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lucasmatiasminzbook.data.local.ticket.Ticket
import com.example.lucasmatiasminzbook.data.local.ticket.TicketRepository
import com.example.lucasmatiasminzbook.data.local.user.Role
import com.example.lucasmatiasminzbook.data.local.user.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class SupportUiState(
    val currentUser: UserEntity? = null,
    val tickets: List<Ticket> = emptyList(),
    val isLoading: Boolean = true,
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

    fun addTicket(subject: String, message: String) {
        viewModelScope.launch {
            val userId = _uiState.value.currentUser?.id ?: return@launch
            val ticket = Ticket(userId = userId, subject = subject, message = message)
            ticketRepository.addTicket(ticket)
        }
    }

    fun addResponse(ticket: Ticket, response: String) {
        viewModelScope.launch {
            ticketRepository.addResponse(ticket, response)
        }
    }

    fun resolveTicket(ticketId: Long) {
        viewModelScope.launch {
            ticketRepository.setResolved(ticketId, true)
        }
    }
}
