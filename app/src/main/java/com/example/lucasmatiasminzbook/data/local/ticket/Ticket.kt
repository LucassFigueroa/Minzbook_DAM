package com.example.lucasmatiasminzbook.data.local.ticket

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class Ticket(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val subject: String,
    val message: String,
    var response: String? = null, // <- Añadido para la respuesta de soporte
    val isResolved: Boolean = false
)
