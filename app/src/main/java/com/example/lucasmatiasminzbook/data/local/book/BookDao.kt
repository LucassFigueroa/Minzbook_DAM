package com.example.lucasmatiasminzbook.data.local.book

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY id DESC")
    fun getAll(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id LIMIT 1")
    fun getById(id: Long): Flow<Book?>

    @Insert
    suspend fun insert(book: Book): Long

    // 👇 agrega esto:
    @Query("SELECT COUNT(*) FROM books")
    suspend fun count(): Int
}

