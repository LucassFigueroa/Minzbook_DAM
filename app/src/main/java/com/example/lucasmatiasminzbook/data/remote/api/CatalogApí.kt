package com.example.lucasmatiasminzbook.data.remote.api

import com.example.lucasmatiasminzbook.data.remote.dto.BookDto
import com.example.lucasmatiasminzbook.data.remote.dto.CreateBookRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface CatalogApi {

    // Lista todos los libros del microservicio
    @GET("catalog/books")
    suspend fun getAllBooks(): List<BookDto>

    // Obtiene un libro por ID
    @GET("catalog/books/{id}")
    suspend fun getBookById(
        @Path("id") id: Long
    ): BookDto

    // Crea un libro (JSON normal)
    @POST("catalog/books")
    suspend fun createBook(
        @Body body: CreateBookRequest
    ): BookDto

    // Elimina un libro, SOLO si X-ROLE = ADMIN
    @DELETE("catalog/books/{id}")
    suspend fun deleteBook(
        @Path("id") id: Long,
        @Header("X-ROLE") role: String
    )
}
