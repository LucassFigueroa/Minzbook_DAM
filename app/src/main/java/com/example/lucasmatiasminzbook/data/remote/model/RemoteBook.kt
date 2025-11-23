package com.example.lucasmatiasminzbook.data.remote.model

data class RemoteBook(
    val id: Long,
    val titulo: String,
    val autor: String,
    val descripcion: String,
    val imagenUrl: String?,
    val precio: Double,
    val stock: Int,
    val categoria: String?,
    val activo: Boolean
)
