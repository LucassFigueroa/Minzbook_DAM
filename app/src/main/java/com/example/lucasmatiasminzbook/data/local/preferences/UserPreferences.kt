package com.example.lucasmatiasminzbook.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val USER_ID = longPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_LASTNAME = stringPreferencesKey("user_lastname")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_ROLE = stringPreferencesKey("user_role")
    }

    // Leer usuario
    val userFlow: Flow<LoggedUser?> = context.dataStore.data.map { prefs ->
        val id = prefs[USER_ID] ?: return@map null

        LoggedUser(
            id = id,
            nombre = prefs[USER_NAME] ?: "",
            apellido = prefs[USER_LASTNAME] ?: "",
            email = prefs[USER_EMAIL] ?: "",
            rol = prefs[USER_ROLE] ?: "USER"
        )
    }

    // Guardar usuario
    suspend fun saveUser(user: LoggedUser) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = user.id
            prefs[USER_NAME] = user.nombre
            prefs[USER_LASTNAME] = user.apellido
            prefs[USER_EMAIL] = user.email
            prefs[USER_ROLE] = user.rol
        }
    }

    // Borrar usuario (logout)
    suspend fun clearUser() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
