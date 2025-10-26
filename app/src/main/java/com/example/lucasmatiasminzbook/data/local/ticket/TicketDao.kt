package com.example.lucasmatiasminzbook.data.local.ticket

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {
    @Insert
    suspend fun insert(ticket: Ticket)

    @Update
    suspend fun update(ticket: Ticket)

    @Query("SELECT * FROM tickets WHERE userId = :userId")
    fun getTicketsByUser(userId: Long): Flow<List<Ticket>>

    @Query("SELECT * FROM tickets")
    fun getAllTickets(): Flow<List<Ticket>>

    @Query("UPDATE tickets SET isResolved = :isResolved WHERE id = :ticketId")
    suspend fun setResolved(ticketId: Long, isResolved: Boolean)
}
