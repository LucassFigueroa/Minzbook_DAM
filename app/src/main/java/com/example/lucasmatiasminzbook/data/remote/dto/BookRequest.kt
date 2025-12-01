package com.example.lucasmatiasminzbook.data.remote.dto

data class BookRequest(
    val titulo: String,
    val autor: String? = null,
    val descripcion: String? = null,
    val precio: Double,
    val stock: Int? = null,
    val categoria: String? = null,
    val imagenUrl: String? = null,

    // NUEVO → portada en Base64 (opcional)
    val coverImageBase64: String? = null,
    val coverContentType: String? = null
)
