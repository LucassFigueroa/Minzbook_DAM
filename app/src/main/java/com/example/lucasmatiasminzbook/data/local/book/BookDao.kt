package com.example.lucasmatiasminzbook.data.local.book

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY id DESC")
    fun getAll(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id LIMIT 1")
    fun getById(id: Long): Flow<Book?>

    @Query("SELECT * FROM books WHERE creatorEmail = :email ORDER BY id DESC")
    fun getByUser(email: String): Flow<List<Book>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(book: Book): Long

    @Query("SELECT COUNT(*) FROM books")
    suspend fun count(): Int

    @Query("DELETE FROM books")
    suspend fun deleteAll()
}
