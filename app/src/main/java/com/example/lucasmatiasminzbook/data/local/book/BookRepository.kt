package com.example.lucasmatiasminzbook.data.local.book

import android.content.Context
import com.example.lucasmatiasminzbook.data.local.database.AppDatabase
import com.example.lucasmatiasminzbook.data.remote.RetrofitClient
import com.example.lucasmatiasminzbook.data.remote.dto.BookDto
import kotlinx.coroutines.flow.Flow

class BookRepository(ctx: Context) {

    private val db = AppDatabase.getInstance(ctx)
    private val bookDao = db.bookDao()
    private val reviewDao = db.reviewDao()

    // =========================================================
    //   LIBROS LOCALES (ROOM)
    // =========================================================

    fun books(): Flow<List<Book>> = bookDao.getAll()

    fun book(id: Long): Flow<Book?> = bookDao.getById(id)

    fun booksByUser(email: String): Flow<List<Book>> = bookDao.getByUser(email)

    suspend fun addBook(
        title: String,
        author: String,
        description: String,
        coverUri: String?,      // sigue sirviendo para libros creados desde galería
        purchasePrice: Int,
        rentPrice: Int,
        creatorEmail: String? = null
    ): Long = bookDao.insert(
        Book(
            title = title,
            author = author,
            description = description,
            coverUri = coverUri,          // local: URI en el dispositivo
            purchasePrice = purchasePrice,
            rentPrice = rentPrice,
            creatorEmail = creatorEmail
        )
    )

    suspend fun deleteBook(bookId: Long) {
        reviewDao.deleteByBookId(bookId)
        bookDao.deleteById(bookId)
    }

    // =========================================================
    //   REEMPLAZAR TODOS LOS LIBROS PARA SINCRONIZAR
    // =========================================================

    suspend fun replaceAll(books: List<Book>) {
        bookDao.clear()            // elimina todo
        bookDao.insertAll(books)   // inserta la nueva lista
    }

    // =========================================================
    //   SINCRONIZAR CON EL BACKEND (CATALOGSERVICE)
    // =========================================================

    suspend fun syncRemoteBooks() {
        try {
            val remoteBooks: List<BookDto> = RetrofitClient.catalogApi.getAllBooks()

            val localBooks = remoteBooks.map { dto ->
                Book(
                    id = dto.id,
                    title = dto.titulo,
                    author = dto.autor,
                    description = dto.descripcion,
                    coverUri = null,                // la portada viene por BLOB desde el micro
                    purchasePrice = dto.precio.toInt(),
                    rentPrice = (dto.precio / 4.0).toInt(),
                    creatorEmail = null
                )
            }

            replaceAll(localBooks)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // =========================================================
    //   RESEÑAS
    // =========================================================

    fun reviewsForBook(bookId: Long): Flow<List<Review>> =
        reviewDao.forBook(bookId)

    fun averageForBook(bookId: Long): Flow<Double?> =
        reviewDao.averageForBook(bookId)

    fun reviewsForUser(email: String): Flow<List<Review>> =
        reviewDao.forUser(email)

    suspend fun addReview(
        bookId: Long,
        userEmail: String,
        userName: String,
        rating: Int,
        comment: String
    ): Long = reviewDao.insert(
        Review(
            bookId = bookId,
            userEmail = userEmail,
            userName = userName,
            rating = rating,
            comment = comment,
            createdAt = System.currentTimeMillis()
        )
    )

    suspend fun deleteReview(review: Review) {
        reviewDao.delete(review)
    }
}
