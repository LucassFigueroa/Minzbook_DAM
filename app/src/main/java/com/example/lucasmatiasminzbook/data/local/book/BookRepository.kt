package com.example.lucasmatiasminzbook.data.local.book

import android.content.Context
import com.example.lucasmatiasminzbook.data.local.database.AppDatabase
import com.example.lucasmatiasminzbook.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow

class BookRepository(ctx: Context) {
    private val db = AppDatabase.getInstance(ctx)
    private val bookDao = db.bookDao()
    private val reviewDao = db.reviewDao()

    // =========================================================
    //   LIBROS LOCALES
    // =========================================================

    fun books(): Flow<List<Book>> = bookDao.getAll()
    fun book(id: Long): Flow<Book?> = bookDao.getById(id)
    fun booksByUser(email: String): Flow<List<Book>> = bookDao.getByUser(email)

    suspend fun addBook(
        title: String,
        author: String,
        description: String,
        coverUri: String?,
        purchasePrice: Int,
        rentPrice: Int,
        creatorEmail: String? = null
    ): Long = bookDao.insert(
        Book(
            title = title,
            author = author,
            description = description,
            coverUri = coverUri,
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
    //   NUEVO: REEMPLAZAR TODOS LOS LIBROS PARA SINCRONIZAR
    // =========================================================
    suspend fun replaceAll(books: List<Book>) {
        bookDao.clear()            // <--- ELIMINA TODO
        bookDao.insertAll(books)   // <--- INSERTA LISTA COMPLETA
    }

    // =========================================================
    //   NUEVO: SINCRONIZAR CON EL BACKEND
    // =========================================================
    suspend fun syncRemoteBooks() {
        try {
            // 🔥 CORREGIDO: USAR catalogApi
            val remoteBooks = RetrofitClient.catalogApi.getAllBooks()

            val localBooks = remoteBooks.map {
                Book(
                    id = it.id,
                    title = it.titulo,
                    author = it.autor,
                    description = it.descripcion,
                    coverUri = it.imagenUrl,  // <-- carga directa desde la URL
                    purchasePrice = it.precio.toInt(),
                    rentPrice = (it.precio / 4).toInt(),
                    creatorEmail = null
                )
            }

            // 🔥 Reemplaza todo en Room
            replaceAll(localBooks)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // =========================================================
    //   RESEÑAS
    // =========================================================

    fun reviewsForBook(bookId: Long): Flow<List<Review>> = reviewDao.forBook(bookId)
    fun averageForBook(bookId: Long): Flow<Double?> = reviewDao.averageForBook(bookId)
    fun reviewsForUser(email: String): Flow<List<Review>> = reviewDao.forUser(email)

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
