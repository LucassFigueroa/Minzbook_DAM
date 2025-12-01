package com.example.lucasmatiasminzbook.data.remote.api

import com.example.lucasmatiasminzbook.data.remote.dto.BookDto
import com.example.lucasmatiasminzbook.data.remote.dto.CreateBookRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.DELETE
import retrofit2.http.PUT

interface CatalogApi {

    @GET("api/catalog/books")
    suspend fun getAllBooks(): List<BookDto>

    @GET("api/catalog/books/{id}")
    suspend fun getBookById(
        @Path("id") id: Long
    ): BookDto

    @POST("api/catalog/books")
    suspend fun createBook(
        @Header("X-ROLE") role: String,
        @Body body: CreateBookRequest
    ): BookDto

    @PUT("api/catalog/books/{id}")
    suspend fun updateBook(
        @Path("id") id: Long,
        @Header("X-ROLE") role: String,
        @Body body: CreateBookRequest
    ): BookDto

    @DELETE("api/catalog/books/{id}")
    suspend fun deleteBook(
        @Path("id") id: Long,
        @Header("X-ROLE") role: String
    )
}
