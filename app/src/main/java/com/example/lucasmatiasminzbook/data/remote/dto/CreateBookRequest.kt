package com.example.lucasmatiasminzbook.data.remote.dto


data class CreateBookRequest(
    val titulo: String,
    val autor: String,
    val categoria: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val imagenUrl: String? = null
)
