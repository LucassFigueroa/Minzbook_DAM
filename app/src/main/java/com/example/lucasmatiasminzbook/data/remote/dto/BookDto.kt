package com.example.lucasmatiasminzbook.data.remote.dto

data class BookDto(
    val id: Long,
    val titulo: String,
    val autor: String,
    val categoria: String,
    val descripcion: String,
    val imagenUrl: String,
    val precio: Double,
    val stock: Int,
    val activo: Boolean
)
