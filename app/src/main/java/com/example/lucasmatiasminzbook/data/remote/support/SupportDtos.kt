
package com.example.lucasmatiasminzbook.data.remote.support

data class SupportTicketDto(
    val id: Long,
    val userId: Long?,
    val email: String,
    val subject: String,
    val message: String,
    val status: String,
    val createdAt: String
)

data class CreateTicketRequest(
    val userId: Long?,
    val email: String,
    val subject: String,
    val message: String
)
