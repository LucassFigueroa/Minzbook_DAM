package com.example.lucasmatiasminzbook.data.remote.dto

data class CreateBookRequest(
    val titulo: String,
    val autor: String,
    val descripcion: String,
    val categoria: String,
    val precio: Double,
    val stock: Int,
    val portadaBase64: String?,       // <- IMPORTANTE: nullable
    val portadaContentType: String?   // <- IMPORTANTE: nullable
)
