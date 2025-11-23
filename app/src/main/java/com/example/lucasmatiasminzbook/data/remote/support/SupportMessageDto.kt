package com.example.lucasmatiasminzbook.data.remote.support

data class SupportMessageDto(
    val id: Long,
    val conversationId: Long,
    val userId: Long,
    val contenido: String,
    val fechaEnvio: String?
)
