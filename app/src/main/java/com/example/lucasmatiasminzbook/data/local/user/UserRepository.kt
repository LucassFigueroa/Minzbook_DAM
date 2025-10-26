package com.example.lucasmatiasminzbook.data.local.user

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import com.example.lucasmatiasminzbook.AuthLocalStore
import com.example.lucasmatiasminzbook.data.local.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

class UserRepository(private val context: Context) {
    private val dao = AppDatabase.getInstance(context).userDao()

    suspend fun register(name: String, email: String, password: String): Result<UserEntity> =
        withContext(Dispatchers.IO) {
            try {
                if (!email.endsWith("@gmail.com")) {
                    return@withContext Result.failure(IllegalArgumentException("Correo inválido"))
                }
                if (password.length < 7) {
                    return@withContext Result.failure(IllegalArgumentException("Contraseña demasiado corta"))
                }

                val existing = dao.findByEmail(email)
                if (existing != null) {
                    return@withContext Result.failure(IllegalStateException("La cuenta ya existe"))
                }

                val user = UserEntity(name = name, email = email, password = password)
                dao.insert(user)
                Result.success(user)
            } catch (e: SQLiteConstraintException) {
                Result.failure(IllegalStateException("La cuenta ya existe"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun login(email: String, password: String): Result<UserEntity> =
        withContext(Dispatchers.IO) {
            try {
                val user = dao.findByEmail(email)
                    ?: return@withContext Result.failure(IllegalArgumentException("La cuenta no existe"))
                if (user.password != password) {
                    return@withContext Result.failure(IllegalArgumentException("Contraseña incorrecta"))
                }
                Result.success(user)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getLoggedInUser(): UserEntity? = withContext(Dispatchers.IO) {
        val email = AuthLocalStore.lastEmail(context) ?: return@withContext null
        dao.findByEmail(email)
    }

    fun getLoggedInUserFlow(): Flow<UserEntity?> {
        val email = AuthLocalStore.lastEmail(context)
        return if (email == null) {
            flowOf(null)
        } else {
            dao.findByEmailFlow(email)
        }
    }
}
