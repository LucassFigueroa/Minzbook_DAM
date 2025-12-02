package com.example.lucasmatiasminzbook.data.remote.dto

data class BookDto(
    val id: Long,
    val titulo: String,
    val autor: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val categoria: String,
    val activo: Boolean,
    val portadaBase64: String?,        // 👈 importante
    val portadaContentType: String?    // 👈 importante
)
