package com.example.lucasmatiasminzbook.data.remote.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// DTO que mandamos al microservicio
data class CreateReviewRequestDto(
    val bookId: Long,
    val userId: Long,
    val rating: Int,
    val comment: String
)

// Lo que devuelve el microservicio
data class ReviewResponseDto(
    val id: Long,
    val bookId: Long,
    val userId: Long,
    val rating: Int,
    val comment: String,
    val activo: Boolean,
    val fechaCreacion: String?,
    val fechaActualizacion: String?
)

interface ReviewApi {

    // POST /reviews
    @POST("reviews")
    suspend fun createReview(
        @Body request: CreateReviewRequestDto
    ): ReviewResponseDto

    // GET /reviews/book/{bookId}  (por si después quieres listarlas desde el backend)
    @GET("reviews/book/{bookId}")
    suspend fun getReviewsByBook(
        @Path("bookId") bookId: Long
    ): List<ReviewResponseDto>
}
