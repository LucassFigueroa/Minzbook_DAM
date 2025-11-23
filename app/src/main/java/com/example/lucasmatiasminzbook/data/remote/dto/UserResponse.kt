package com.example.lucasmatiasminzbook.data.remote.dto

data class UserResponse(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val email: String,
    val rol: String
)
