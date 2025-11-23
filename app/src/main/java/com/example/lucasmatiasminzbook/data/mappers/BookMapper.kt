package com.example.lucasmatiasminzbook.data.mappers

import com.example.lucasmatiasminzbook.data.local.book.Book
import com.example.lucasmatiasminzbook.data.remote.dto.BookDto

fun BookDto.toLocalBook(): Book = Book(
    id = id,                         // usamos el id del backend
    title = titulo,
    author = autor,
    description = descripcion,
    coverUri = imagenUrl,           // URL remota
    coverResourceId = null,
    purchasePrice = precio.toInt(),
    rentPrice = (precio * 0.25).toInt(), // ej: arriendo = 25% del precio
    creatorEmail = null
)
