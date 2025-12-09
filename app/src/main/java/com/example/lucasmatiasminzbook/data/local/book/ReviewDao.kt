package com.example.lucasmatiasminzbook.data.local.book

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    // Reseñas de un libro (detalle del libro)
    @Query("SELECT * FROM reviews WHERE bookId = :bookId ORDER BY createdAt DESC")
    fun forBook(bookId: Long): Flow<List<Review>>

    // Promedio de estrellas de un libro (detalle / listado)
    @Query("SELECT AVG(rating * 1.0) FROM reviews WHERE bookId = :bookId")
    fun averageForBook(
        bookId: Long): Flow<Double?>

    // Todas las reseñas de un usuario (pantalla \'Calificaciones\')
    @Query("SELECT * FROM reviews WHERE userEmail = :email ORDER BY createdAt DESC")
    fun forUser(email: String): Flow<List<Review>>

    // Inserta reseña
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(review: Review): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reviews: List<Review>)

    // (Opcional) limpiar tabla en pruebas
    @Query("DELETE FROM reviews")
    suspend fun deleteAll()

    @Query("DELETE FROM reviews WHERE bookId = :bookId")
    suspend fun deleteByBookId(bookId: Long)

    @Delete
    suspend fun delete(review: Review)
}
