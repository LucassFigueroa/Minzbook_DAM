package com.example.lucasmatiasminzbook.data.remote.repository

import com.example.lucasmatiasminzbook.data.remote.api.SupportApi
import com.example.lucasmatiasminzbook.data.remote.support.*

class SupportRepository(
    private val api: SupportApi
) {

    suspend fun createConversation(
        userId: Long,
        asunto: String,
        mensaje: String
    ): SupportConversationDto {
        val body = CreateConversationRequest(
            userId = userId,
            asunto = asunto,
            mensajeInicial = mensaje
        )
        return api.createConversation(body)
    }

    suspend fun getUserConversations(userId: Long): List<SupportConversationDto> {
        return api.getConversationsForUser(userId)
    }

    suspend fun sendMessage(
        conversationId: Long,
        userId: Long,
        contenido: String
    ): SupportMessageDto {
        val body = SendMessageRequest(conversationId, userId, contenido)
        return api.sendMessage(body)
    }

    suspend fun getMessages(conversationId: Long): List<SupportMessageDto> {
        return api.getMessages(conversationId)
    }

    suspend fun closeConversation(id: Long): SupportConversationDto {
        return api.closeConversation(id)
    }
    suspend fun getAllConversations(): List<SupportConversationDto> {
        return api.getAllConversations()
    }

}
