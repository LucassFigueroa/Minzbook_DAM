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

    suspend fun addBook(
        title: String,
        author: String,
        description: String,
        coverUri: String?,
        purchasePrice: Int,
        rentPrice: Int
    ): Long = bookDao.insert(
        Book(
            title = title,
            author = author,
            description = description, // 👈 usa description
            coverUri = coverUri,
            purchasePrice = purchasePrice,
            rentPrice = rentPrice
        )
    )

    // 👇 Siembra segura (evita crashes por carreras)
    suspend fun ensureSeeded() {
        if (bookDao.count() == 0) {
            val base = listOf(
                Triple("Libro 1", "Lukkk", "Una historia de aventura y amistad en un mundo fantástico."),
                Triple("Libro 2", "Skadi", "Relatos del norte: mitos, hielo y valor."),
                Triple("Libro 3", "Heavy", "Crónicas de metal y fuego en tierras lejanas."),
                Triple("Libro 4", "Crown", "Intrigas y coronas: el destino de un reino."),
                Triple("Libro 5", "Pepe", "Comedia y ternura en un barrio inolvidable.")
            )
            base.forEach { (t, a, d) ->
                addBook(
                    title = t,
                    author = a,
                    description = d,
                    coverUri = null,
                    purchasePrice = 12000,
                    rentPrice = 6800
                )
            }
        }
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
