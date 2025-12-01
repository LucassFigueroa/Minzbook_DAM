package com.example.lucasmatiasminzbook.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.webkit.MimeTypeMap
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageUtils {

    /**
     * Convierte un Uri (de la galería) en:
     *  - String Base64
     *  - contentType (image/jpeg, image/png, etc.)
     */
    fun uriToBase64(context: Context, uri: Uri): Pair<String, String>? {
        return try {
            val contentResolver = context.contentResolver

            // Intentamos obtener el tipo MIME
            val typeFromResolver = contentResolver.getType(uri)
            val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            val guessedFromExt = if (!extension.isNullOrBlank()) {
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            } else null

            val contentType = typeFromResolver ?: guessedFromExt ?: "image/jpeg"

            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val buffer = ByteArrayOutputStream()
            val data = ByteArray(1024)
            var nRead: Int

            if (inputStream != null) {
                while (inputStream.read(data).also { nRead = it } != -1) {
                    buffer.write(data, 0, nRead)
                }
                inputStream.close()
            }

            val bytes = buffer.toByteArray()

            // En Android usa android.util.Base64 para asegurar compatibilidad
            val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)

            Pair(base64, contentType)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Convierte un String Base64 a Bitmap para mostrar en ImageView / Compose.
     */
    fun base64ToBitmap(base64: String): Bitmap? {
        return try {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}