package com.example.lucasmatiasminzbook.data.local.book

import android.content.Context
import com.example.lucasmatiasminzbook.data.local.database.AppDatabase
import com.example.lucasmatiasminzbook.data.remote.RetrofitClient
import com.example.lucasmatiasminzbook.data.remote.api.CreateReviewRequestDto
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class BookRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val bookDao = db.bookDao()
    private val reviewDao = db.reviewDao()
    private val reviewApi = RetrofitClient.reviewApi

    // =========================
    // LIBROS LOCALES
    // =========================
    fun books(): Flow<List<Book>> = bookDao.getAll()

    fun book(bookId: Long): Flow<Book?> = bookDao.getBookById(bookId)

    suspend fun replaceAll(books: List<Book>) {
        bookDao.deleteAll()
        bookDao.insertAll(books)
    }

    // =========================
    // RESEÑAS LOCALES (ROOM)
    // =========================
    fun reviewsForBook(bookId: Long): Flow<List<Review>> =
        reviewDao.forBook(bookId)

    fun averageForBook(bookId: Long): Flow<Double?> =
        reviewDao.averageForBook(bookId)

    suspend fun deleteReview(review: Review) {
        reviewDao.delete(review)
    }

    private fun parseIsoDate(isoDate: String?): Long {
        if (isoDate.isNullOrBlank()) {
            return System.currentTimeMillis()
        }
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
            format.parse(isoDate)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            try {
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                format.timeZone = TimeZone.getTimeZone("UTC")
                format.parse(isoDate)?.time ?: System.currentTimeMillis()
            } catch (e2: Exception) {
                System.currentTimeMillis()
            }
        }
    }

    // =========================
    // SYNC DESDE MICROSERVICIO
    // =========================
    suspend fun syncReviewsFromMicro(bookId: Long) {
        val remoteList = reviewApi.getReviewsByBook(bookId)
        val localList = remoteList.map { dto ->
            Review(
                id = dto.id,
                bookId = dto.bookId,
                userEmail = "user_${dto.userId}@example.com", // Placeholder
                userName = "Usuario ${dto.userId}",
                rating = dto.rating,
                comment = dto.comment,
                createdAt = parseIsoDate(dto.fechaCreacion)
            )
        }
        reviewDao.deleteByBookId(bookId)
        reviewDao.insertAll(localList) // Assumes insertAll exists in ReviewDao
    }

    // =========================
    // CREAR RESEÑA REMOTA + LOCAL
    // =========================
    suspend fun addReview(
        bookId: Long,
        userId: Long,
        userEmail: String,
        userName: String,
        rating: Int,
        comment: String
    ) {
        val created = reviewApi.createReview(
            CreateReviewRequestDto(
                bookId = bookId,
                userId = userId,
                rating = rating,
                comment = comment
            )
        )

        val local = Review(
            id = created.id,
            bookId = created.bookId,
            userEmail = userEmail,
            userName = userName,
            rating = created.rating,
            comment = created.comment,
            createdAt = parseIsoDate(created.fechaCreacion)
        )
        reviewDao.insert(local)
    }
}
