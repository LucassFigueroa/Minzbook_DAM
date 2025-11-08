package com.example.lucasmatiasminzbook

import android.content.Context
import android.net.Uri

object AuthLocalStore {

    // Nombre del archivo de preferencias compartidas donde se guardan los datos.
    private const val PREFS = "auth_local_store"

    // Clave para guardar el email del último usuario que inició sesión.
    private const val KEY_LAST_EMAIL = "last_email"

    // Clave para guardar el nombre visible del último usuario.
    private const val KEY_LAST_NAME = "last_name"

    // Clave para guardar la URI de la foto de perfil como String.
    private const val KEY_PROFILE_PHOTO_URI = "profile_photo_uri"

    // Clave para guardar la preferencia de "Mantener sesión iniciada".
    private const val KEY_REMEMBER_ME = "remember_me"

    fun setSession(context: Context, email: String, visibleName: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_LAST_EMAIL, email)
            .putString(KEY_LAST_NAME, visibleName)
            .apply()
    }

    fun lastEmail(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_LAST_EMAIL, null)

    fun lastName(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_LAST_NAME, null)

    fun clearSession(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .clear()
            .apply()
    }

    fun setProfilePhotoUri(context: Context, uriString: String?) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_PROFILE_PHOTO_URI, uriString)
            .apply()
    }

    fun profilePhotoUri(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_PROFILE_PHOTO_URI, null)

    fun lastPhotoUri(context: Context): String? = profilePhotoUri(context)

    fun profilePhotoAsUri(context: Context): Uri? =
        profilePhotoUri(context)?.let { Uri.parse(it) }

    fun setRememberMe(context: Context, value: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_REMEMBER_ME, value)
            .apply()
    }

    fun isRememberMe(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_REMEMBER_ME, false)
}
