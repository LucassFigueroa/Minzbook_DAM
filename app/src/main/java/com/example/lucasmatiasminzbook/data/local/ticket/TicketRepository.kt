package com.example.lucasmatiasminzbook.data.local.ticket

import kotlinx.coroutines.flow.Flow

class TicketRepository(private val ticketDao: TicketDao) {

    fun getTicketsByUser(userId: Long): Flow<List<Ticket>> = ticketDao.getTicketsByUser(userId)

    fun getAllTickets(): Flow<List<Ticket>> = ticketDao.getAllTickets()

    fun getMessagesForTicket(ticketId: Long): Flow<List<TicketMessage>> = ticketDao.getMessagesForTicket(ticketId)

    suspend fun addTicket(ticket: Ticket, firstMessage: TicketMessage) {
        val ticketId = ticketDao.insertTicket(ticket)
        ticketDao.insertMessage(firstMessage.copy(ticketId = ticketId))
    }

    suspend fun addMessage(message: TicketMessage) {
        ticketDao.insertMessage(message)
    }

    suspend fun setResolved(ticketId: Long, isResolved: Boolean) {
        ticketDao.setResolved(ticketId, isResolved)
    }
}
