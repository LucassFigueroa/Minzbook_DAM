package com.example.lucasmatiasminzbook.data.remote.support

data class CreateConversationRequest(
    val userId: Long,
    val asunto: String,
    val mensajeInicial: String
)
