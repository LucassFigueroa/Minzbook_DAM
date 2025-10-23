package com.example.lucasmatiasminzbook.ui.catalog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lucasmatiasminzbook.data.local.book.Book
import com.example.lucasmatiasminzbook.data.local.book.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CatalogViewModel(app: Application): AndroidViewModel(app) {
    private val repo = BookRepository(app)

    val books: Flow<List<Book>> = repo.books()

    init {
        // Siembra inicial si no hay libros
        viewModelScope.launch {
            val current = repo.books() // Flow
            // lee una vez
            current.collect { list ->
                if (list.isEmpty()) {
                    seed()
                }
                // parar después de primer valor
                return@collect
            }
        }
    }

    private suspend fun seed() {
        val base = listOf(
            Triple("Libro 1", "Lukkk", "Una historia de aventura y amistad en un mundo fantástico."),
            Triple("Libro 2", "Skadi", "Relatos del norte: mitos, hielo y valor."),
            Triple("Libro 3", "Heavy", "Crónicas de metal y fuego en tierras lejanas."),
            Triple("Libro 4", "Crown", "Intrigas y coronas: el destino de un reino."),
            Triple("Libro 5", "Pepe", "Comedia y ternura en un barrio inolvidable.")
        )
        base.forEach { (title, author, description) ->
            repo.addBook(
                title = title,
                author = author,
                description = description,
                coverUri = null,
                purchasePrice = 12000,
                rentPrice = 6800
            )
        }
    }
}
