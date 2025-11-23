package com.example.lucasmatiasminzbook.data.remote.api

import com.example.lucasmatiasminzbook.data.remote.support.CreateConversationRequest
import com.example.lucasmatiasminzbook.data.remote.support.SupportConversationDto
import com.example.lucasmatiasminzbook.data.remote.support.SendMessageRequest
import com.example.lucasmatiasminzbook.data.remote.support.SupportMessageDto
import retrofit2.http.*

interface SupportApi {

    // Crear conversación (equivalente a "crear ticket")
    @POST("support/conversations")
    suspend fun createConversation(
        @Body request: CreateConversationRequest
    ): SupportConversationDto

    // Obtener conversaciones por usuario
    @GET("support/conversations/user/{userId}")
    suspend fun getConversationsForUser(
        @Path("userId") userId: Long
    ): List<SupportConversationDto>

    // Enviar mensaje dentro de una conversación
    @POST("support/messages")
    suspend fun sendMessage(
        @Body request: SendMessageRequest
    ): SupportMessageDto

    // Obtener mensajes de la conversación
    @GET("support/messages/{conversationId}")
    suspend fun getMessages(
        @Path("conversationId") conversationId: Long
    ): List<SupportMessageDto>

    // Cerrar ticket (conversación)
    @PATCH("support/conversations/{id}/close")
    suspend fun closeConversation(
        @Path("id") id: Long
    ): SupportConversationDto

    @GET("support/conversations")
    suspend fun getAllConversations(): List<SupportConversationDto>

}
