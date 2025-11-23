package com.example.lucasmatiasminzbook.data.remote.support

data class SendMessageRequest(
    val conversationId: Long,
    val userId: Long,
    val contenido: String
)
