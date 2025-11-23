package com.example.lucasmatiasminzbook.data.local.preferences

data class LoggedUser(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val email: String,
    val rol: String
)
