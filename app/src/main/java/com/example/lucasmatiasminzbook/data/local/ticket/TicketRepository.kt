package com.example.lucasmatiasminzbook.data.local.ticket

import kotlinx.coroutines.flow.Flow

class TicketRepository(private val ticketDao: TicketDao) {

    fun getTicketsByUser(userId: Long): Flow<List<Ticket>> {
        return ticketDao.getTicketsByUser(userId)
    }

    fun getAllTickets(): Flow<List<Ticket>> {
        return ticketDao.getAllTickets()
    }

    suspend fun addTicket(ticket: Ticket) {
        ticketDao.insert(ticket)
    }

    suspend fun addResponse(ticket: Ticket, response: String) {
        ticket.response = response
        ticketDao.update(ticket)
    }

    suspend fun setResolved(ticketId: Long, isResolved: Boolean) {
        ticketDao.setResolved(ticketId, isResolved)
    }
}
