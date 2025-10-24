package com.example.lucasmatiasminzbook.data.local.book

import android.content.Context
import com.example.lucasmatiasminzbook.R
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

    // 👇 Siembra segura (evita crashes por carreras)
    suspend fun ensureSeeded() {
        // FORZAR SIEMBRA PARA PRUEBAS (elimina esto en producción)
        reviewDao.deleteAll()
        bookDao.deleteAll()

        if (bookDao.count() == 0) {
            val book1Id = bookDao.insert(Book(title = "Informatica en 8 pasos", author = "Skadi", description = "Aprende informática desde cero.", coverResourceId = R.drawable.portada1, purchasePrice = 10000, rentPrice = 2500))
            val book2Id = bookDao.insert(Book(title = "Poder Tecnologico", author = "Heavy", description = "La tecnología es poder.", coverResourceId = R.drawable.portada2, purchasePrice = 15000, rentPrice = 3000))
            val book3Id = bookDao.insert(Book(title = "Aprende a codear", author = "Crown", description = "El arte de la programación.", coverResourceId = R.drawable.portada3, purchasePrice = 20000, rentPrice = 4000))
            val book4Id = bookDao.insert(Book(title = "Java si Python no", author = "Victor", description = "¿Java o Python? El eterno debate.", coverResourceId = R.drawable.portada4, purchasePrice = 25000, rentPrice = 5000))
            bookDao.insert(Book(title = "lolsito la odisea", author = "Lukkk", description = "Una aventura épica en el mundo de los videojuegos.", coverResourceId = R.drawable.portada5, purchasePrice = 30000, rentPrice = 6000))

            val reviews = listOf(
                Review(bookId = book1Id, userEmail = "Incognito@gmail.com", userName = "Incognito", rating = 5, comment = "¡Excelente libro!", createdAt = System.currentTimeMillis()),
                Review(bookId = book1Id, userEmail = "Incognito2@gmail.com", userName = "Incognito2", rating = 4, comment = "Muy bueno, pero podría ser mejor.", createdAt = System.currentTimeMillis()),
                Review(bookId = book2Id, userEmail = "Incognito3@gmail.com", userName = "Incognito3", rating = 3, comment = "No está mal, pero no me enganchó.", createdAt = System.currentTimeMillis()),
                Review(bookId = book4Id, userEmail = "Incognito4@gmail.com", userName = "Alberto", rating = 2, comment = "No me gustó, soy python lover !!!! *se enoja*", createdAt = System.currentTimeMillis()),
                Review(bookId = book3Id, userEmail = "Incognito5@gmail.com", userName = "Incognito5", rating = 1, comment = "¡Malísimo!", createdAt = System.currentTimeMillis())
            )

            reviews.forEach { reviewDao.insert(it) }
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
