package com.example.lucasmatiasminzbook.data.remote.repository

import com.example.lucasmatiasminzbook.data.remote.RetrofitClient
import com.example.lucasmatiasminzbook.data.remote.dto.BookDto

class CatalogRepository {

    private val api = RetrofitClient.catalogApi

    suspend fun getBooks(): Result<List<BookDto>> {
        return try {
            val books = api.getAllBooks()
            Result.success(books)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
