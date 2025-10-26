package com.example.lucasmatiasminzbook.data.local.book

import android.content.Context
import com.example.lucasmatiasminzbook.data.local.database.AppDatabase
import kotlinx.coroutines.flow.Flow

class BookRepository(ctx: Context) {
    private val db = AppDatabase.getInstance(ctx)
    private val bookDao = db.bookDao()
    private val reviewDao = db.reviewDao()

    // Libros
    fun books(): Flow<List<Book>> = bookDao.getAll()
    fun book(id: Long): Flow<Book?> = bookDao.getById(id)
    fun booksByUser(email:String): Flow<List<Book>> = bookDao.getByUser(email)


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

    suspend fun deleteReview(review: Review) {
        reviewDao.delete(review)
    }

    // Reseñas
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
}
