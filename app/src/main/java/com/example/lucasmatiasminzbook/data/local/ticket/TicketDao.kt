package com.example.lucasmatiasminzbook.data.local.ticket

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {
    // Inserta una nueva conversación y devuelve su ID
    @Insert
    suspend fun insertTicket(ticket: Ticket): Long

    // Inserta un nuevo mensaje
    @Insert
    suspend fun insertMessage(message: TicketMessage)

    // Obtiene todos los mensajes de una conversación específica
    @Query("SELECT * FROM ticket_messages WHERE ticketId = :ticketId ORDER BY timestamp ASC")
    fun getMessagesForTicket(ticketId: Long): Flow<List<TicketMessage>>

    // Obtiene todas las conversaciones de un usuario
    @Query("SELECT * FROM tickets WHERE userId = :userId ORDER BY id DESC")
    fun getTicketsByUser(userId: Long): Flow<List<Ticket>>

    // Obtiene todas las conversaciones (para el equipo de soporte)
    @Query("SELECT * FROM tickets ORDER BY id DESC")
    fun getAllTickets(): Flow<List<Ticket>>

    // Marca una conversación como resuelta
    @Query("UPDATE tickets SET isResolved = :isResolved WHERE id = :ticketId")
    suspend fun setResolved(ticketId: Long, isResolved: Boolean)
}
