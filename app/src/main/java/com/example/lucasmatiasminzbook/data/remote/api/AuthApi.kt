package com.example.lucasmatiasminzbook.data.remote.api

import com.example.lucasmatiasminzbook.data.remote.dto.UserLoginRequest
import com.example.lucasmatiasminzbook.data.remote.dto.UserRegisterRequest
import com.example.lucasmatiasminzbook.data.remote.dto.UserResponse
import retrofit2.http.*

interface AuthApi {

    @POST("auth/login")
    suspend fun login(
        @Body body: UserLoginRequest
    ): UserResponse

    @POST("auth/register")
    suspend fun register(
        @Body body: UserRegisterRequest
    ): UserResponse

    @PATCH("auth/role/{id}")
    suspend fun assignRole(
        @Path("id") userId: Long,
        @Header("X-ADMIN-KEY") adminKey: String,
        @Body body: Map<String, String> // { "rol": "ADMIN" }
    )
}
