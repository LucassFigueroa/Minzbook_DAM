package com.example.lucasmatiasminzbook

import android.content.Context
import androidx.core.content.edit

object AuthLocalStore {
    private const val FILE = "minzbook_prefs"
    private const val K_EMAIL = "last_email"
    private const val K_NAME = "last_name"
    private const val K_BIOMETRIC = "biometric_enabled"

    fun setSession(ctx: Context, email: String, name: String) {
        ctx.getSharedPreferences(FILE, 0).edit {
            putString(K_EMAIL, email)
            putString(K_NAME, name)
        }
    }
    fun clearSession(ctx: Context) {
        ctx.getSharedPreferences(FILE, 0).edit {
            remove(K_EMAIL); remove(K_NAME)
        }
    }
    fun lastEmail(ctx: Context): String? =
        ctx.getSharedPreferences(FILE, 0).getString(K_EMAIL, null)

    fun lastName(ctx: Context): String? =
        ctx.getSharedPreferences(FILE, 0).getString(K_NAME, null)

    fun setBiometricEnabled(ctx: Context, enabled: Boolean) {
        ctx.getSharedPreferences(FILE, 0).edit { putBoolean(K_BIOMETRIC, enabled) }
    }
    fun isBiometricEnabled(ctx: Context): Boolean =
        ctx.getSharedPreferences(FILE, 0).getBoolean(K_BIOMETRIC, false)
}
