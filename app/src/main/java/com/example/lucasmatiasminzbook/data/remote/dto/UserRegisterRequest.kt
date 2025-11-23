package com.example.lucasmatiasminzbook.data.remote.dto

data class UserRegisterRequest(
    val nombre: String,
    val apellido: String,
    val email: String,
    val password: String
)
