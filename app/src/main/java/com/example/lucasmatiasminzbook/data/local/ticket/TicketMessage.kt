package com.example.lucasmatiasminzbook.data.local.ticket

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MessageAuthor { USER, SUPPORT }

@Entity(tableName = "ticket_messages")
data class TicketMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ticketId: Long, // ID de la conversación a la que pertenece
    val author: MessageAuthor, // Quién envió el mensaje
    val message: String, // El contenido del mensaje
    val timestamp: Long = System.currentTimeMillis() // La hora en que se envió
)
