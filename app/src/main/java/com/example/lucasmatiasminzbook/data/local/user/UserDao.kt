package com.example.lucasmatiasminzbook.data.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    fun findByEmailFlow(email: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE) // <
    suspend fun insert(user: UserEntity)
}
