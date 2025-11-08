package com.example.lucasmatiasminzbook

import android.app.Application
import com.example.lucasmatiasminzbook.data.AppContainer
import com.example.lucasmatiasminzbook.data.AppDataContainer
import com.example.lucasmatiasminzbook.data.local.book.Book
import com.example.lucasmatiasminzbook.data.local.book.Review
import com.example.lucasmatiasminzbook.data.local.database.AppDatabase
import com.example.lucasmatiasminzbook.data.local.user.Role
import com.example.lucasmatiasminzbook.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MinzbookApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        seedInitialData()
    }

    private fun seedInitialData() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(this@MinzbookApplication)
            val userDao = db.userDao()
            val bookDao = db.bookDao()
            val reviewDao = db.reviewDao()

            // Usuarios
            userDao.insert(UserEntity(name = "Soporte", email = "soporte@gmail.com", password = "!Support1", role = Role.SUPPORT))
            userDao.insert(UserEntity(name = "Moderador", email = "mod@gmail.com", password = "!Moderator1", role = Role.MODERATOR))
            userDao.insert(UserEntity(name = "Lucas", email = "luc@gmail.com", password = "!Lucas123", role = Role.USER))

            // Libros y Reseñas
            if (bookDao.count() == 0) {
                val book1Id = bookDao.insert(Book(title = "Informatica en 8 pasos", author = "Skadi", description = "Aprende informática desde cero.", coverResourceId = R.drawable.portada1, purchasePrice = 10000, rentPrice = 2500))
                val book2Id = bookDao.insert(Book(title = "Poder Tecnologico", author = "Heavy", description = "La tecnología es poder.", coverResourceId = R.drawable.portada2, purchasePrice = 15000, rentPrice = 3000))
                val book3Id = bookDao.insert(Book(title = "Aprende a codear", author = "Crown", description = "El arte de la programación.", coverResourceId = R.drawable.portada3, purchasePrice = 20000, rentPrice = 4000))
                val book4Id = bookDao.insert(Book(title = "Java si Python no", author = "Victor", description = "¿Java o Python? El eterno debate.", coverResourceId = R.drawable.portada4, purchasePrice = 25000, rentPrice = 5000))
                val book5Id = bookDao.insert(Book(title = "lolsito la odisea", author = "Lukkk", description = "Una aventura épica en el mundo de los videojuegos.", coverResourceId = R.drawable.portada5, purchasePrice = 30000, rentPrice = 6000))

                val reviews = listOf(
                    Review(bookId = book1Id, userEmail = "Incognito@gmail.com", userName = "Incognito", rating = 5, comment = "¡Excelente libro!, me ayudo a pasar desarrollo fullstack!", createdAt = System.currentTimeMillis()),
                    Review(bookId = book2Id, userEmail = "Incognito2@gmail.com", userName = "Incognito2", rating = 4, comment = "Muy bueno, pero podría ser mejor.", createdAt = System.currentTimeMillis()),
                    Review(bookId = book3Id, userEmail = "Incognito3@gmail.com", userName = "Incognito3", rating = 3, comment = "No está mal, pero no me enganchó, soy mas de ver videos en yt", createdAt = System.currentTimeMillis()),
                    Review(bookId = book4Id, userEmail = "Incognito4@gmail.com", userName = "Alberto", rating = 2, comment = "No me gustó, soy python lover !!!! *se enoja*", createdAt = System.currentTimeMillis()),
                    Review(bookId = book5Id, userEmail = "Incognito5@gmail.com", userName = "Incognito5", rating = 1, comment = "¡Malísimo!, este juego arruino mi vida, saquenme del looool", createdAt = System.currentTimeMillis())
                )

                reviews.forEach { reviewDao.insert(it) }
            }
        }
    }
}
