package com.example.lucasmatiasminzbook.data.local.ticket

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa una conversación de soporte (un ticket).
 * Los mensajes individuales de esta conversación se guardan en la tabla TicketMessage.
 */
@Entity(tableName = "tickets")
data class Ticket(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val subject: String, // El asunto o título de la conversación
    val isResolved: Boolean = false
)
