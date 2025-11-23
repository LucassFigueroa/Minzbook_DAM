package com.example.lucasmatiasminzbook.data.local.book

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM books")
    fun getAll(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id")
    fun getById(id: Long): Flow<Book?>

    @Query("SELECT * FROM books WHERE creatorEmail = :email")
    fun getByUser(email: String): Flow<List<Book>>

    @Insert
    suspend fun insert(book: Book): Long


    @Query("DELETE FROM books")
    suspend fun clear()

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<Book>)

}
