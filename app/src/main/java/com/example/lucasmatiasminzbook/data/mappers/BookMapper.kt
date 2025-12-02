package com.example.lucasmatiasminzbook.data.remote.dto

import com.example.lucasmatiasminzbook.data.local.book.Book

/**
 * Mapea un BookDto (remoto, con BLOB en el backend) a un Book local (Room).
 * Por ahora NO reconstruimos la portada desde el BLOB, así que coverUri = null.
 */
fun BookDto.toLocalBook(): Book =
    Book(
        id = this.id,
        title = this.titulo,
        author = this.autor,
        description = this.descripcion,
        coverUri = null, // la portada viene como BLOB en el backend, aquí solo texto
        purchasePrice = this.precio.toInt(),
        rentPrice = (this.precio / 4).toInt(),
        creatorEmail = null
    )

/**
 * Opcional: de Book local a BookDto.
 * Sirve solo si en algún momento necesitas enviar algo similar hacia el backend.
 * Rellenamos campos que el local no tiene con defaults razonables.
 */
fun Book.toBookDto(): BookDto =
    BookDto(
        id = this.id,
        titulo = this.title,
        autor = this.author,
        descripcion = this.description,
        categoria = "General",      // el Book local no guarda categoría, ponemos un default
        precio = this.purchasePrice.toDouble(),
        stock = 1,                  // el local no guarda stock, default 1
        activo = true,              // local tampoco guarda "activo", asumimos true
        portadaBase64 = null,       // local no guarda el BLOB
        portadaContentType = null   // local tampoco guarda el content-type
    )
