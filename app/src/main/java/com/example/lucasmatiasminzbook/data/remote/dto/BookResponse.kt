package com.example.lucasmatiasminzbook.data.remote.dto

data class BookResponse(
    val id: Long,
    val titulo: String,
    val autor: String?,
    val descripcion: String?,
    val precio: Double,
    val stock: Int?,
    val categoria: String?,
    val imagenUrl: String?,
    val activo: Boolean,

    // NUEVO → si el libro tiene portada en BD
    val coverImageBase64: String? = null,
    val coverContentType: String? = null
)
