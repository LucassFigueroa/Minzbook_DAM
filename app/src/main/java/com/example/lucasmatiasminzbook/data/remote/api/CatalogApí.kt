package com.example.lucasmatiasminzbook.data.remote.api

import com.example.lucasmatiasminzbook.data.remote.dto.BookDto
import retrofit2.http.GET

interface CatalogApi {

    // GET http://10.0.2.2:8082/catalog/books
    @GET("catalog/books")
    suspend fun getAllBooks(): List<BookDto>
}
