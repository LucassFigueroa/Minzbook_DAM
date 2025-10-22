package com.example.lucasmatiasminzbook.ui.catalog

import androidx.lifecycle.ViewModel
import com.example.lucasmatiasminzbook.data.Book
import com.example.lucasmatiasminzbook.data.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CatalogViewModel : ViewModel() {

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    init {
        // Cargamos el mock por ahora
        _books.value = BookRepository.initialBooks()
    }
}
