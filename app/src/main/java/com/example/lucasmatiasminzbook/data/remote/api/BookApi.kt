package com.example.lucasmatiasminzbook.data.remote.api

import com.example.lucasmatiasminzbook.data.remote.model.RemoteBook
import retrofit2.http.GET

interface BookApi {

    @GET("/catalog/books")
    suspend fun getAllBooks(): List<RemoteBook>

}
