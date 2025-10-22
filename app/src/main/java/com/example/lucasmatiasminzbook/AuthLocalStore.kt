package com.example.lucasmatiasminzbook

import android.content.Context
import android.content.Context.MODE_PRIVATE

object AuthLocalStore {
    private const val PREF = "auth"

    private fun sp(ctx: Context) = ctx.getSharedPreferences(PREF, MODE_PRIVATE)

    fun userExists(ctx: Context, email: String): Boolean {
        return sp(ctx).contains("user:$email")
    }

    fun register(ctx: Context, email: String, password: String): Result<String> {
        val prefs = sp(ctx)
        if (userExists(ctx, email)) {
            return Result.failure(IllegalStateException("Ya existe una cuenta con este correo"))
        }
        val displayName = email.substringBefore("@").replaceFirstChar { it.uppercase() }
        prefs.edit()
            .putString("user:$email", password)
            .putString("displayName:$email", displayName)
            .apply()
        return Result.success(displayName)
    }

    fun validateLogin(ctx: Context, email: String, password: String): Result<String> {
        val prefs = sp(ctx)
        val stored = prefs.getString("user:$email", null)
        if (stored == null) {
            return Result.failure(IllegalArgumentException("Esa cuenta no existe. Regístrate para continuar."))
        }
        if (stored != password) {
            return Result.failure(IllegalArgumentException("Contraseña incorrecta."))
        }
        val displayName = prefs.getString("displayName:$email", email.substringBefore("@"))!!
        return Result.success(displayName)
    }

    fun setSession(ctx: Context, email: String, displayName: String) {
        sp(ctx).edit()
            .putBoolean("isAuthenticated", true)
            .putString("email", email)
            .putString("displayName", displayName)
            .apply()
    }

    fun clearSession(ctx: Context) {
        sp(ctx).edit()
            .putBoolean("isAuthenticated", false)
            .remove("email")
            .remove("displayName")
            .apply()
    }
}
