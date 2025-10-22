package com.example.lucasmatiasminzbook.data

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val synopsis: String
    // Luego agregaremos: price, rating, imageUrl/coverResId, etc.
)