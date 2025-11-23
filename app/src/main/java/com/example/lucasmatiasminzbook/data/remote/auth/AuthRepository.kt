package com.example.lucasmatiasminzbook.data.remote.auth

import com.example.lucasmatiasminzbook.data.remote.RetrofitClient
import com.example.lucasmatiasminzbook.data.remote.dto.UserLoginRequest
import com.example.lucasmatiasminzbook.data.remote.dto.UserRegisterRequest
import com.example.lucasmatiasminzbook.data.remote.dto.UserResponse

class AuthRepository {

    suspend fun login(email: String, password: String): UserResponse {
        return RetrofitClient.authApi.login(
            UserLoginRequest(email, password)
        )
    }

    suspend fun register(
        nombre: String,
        apellido: String,
        email: String,
        password: String
    ): UserResponse {
        return RetrofitClient.authApi.register(
            UserRegisterRequest(nombre, apellido, email, password)
        )
    }

    suspend fun assignRole(
        userId: Long,
        role: String,
        adminKey: String
    ) {
        RetrofitClient.authApi.assignRole(
            userId,
            adminKey,
            mapOf("rol" to role)
        )
    }
}
