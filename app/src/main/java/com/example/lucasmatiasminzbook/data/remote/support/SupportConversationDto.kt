package com.example.lucasmatiasminzbook.data.remote.support

data class SupportConversationDto(
    val id: Long,
    val userId: Long,
    val asunto: String,
    val status: String,
    val fechaCreacion: String?,
    val fechaActualizacion: String?
)
