package com.example.lucasmatiasminzbook.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lucasmatiasminzbook.data.local.book.Book
import com.example.lucasmatiasminzbook.data.local.book.BookRepository
import com.example.lucasmatiasminzbook.data.remote.RetrofitClient
import com.example.lucasmatiasminzbook.data.remote.dto.BookDto
import com.example.lucasmatiasminzbook.data.remote.dto.toLocalBook
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CatalogUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val books: List<BookDto> = emptyList()
)

class CatalogViewModel(
    private val localRepo: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState

    fun loadBooks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // 1) Traemos libros del microservicio (remoto)
                val remote: List<BookDto> = RetrofitClient.catalogApi.getAllBooks()

                // 2) Los convertimos a entidad local Book (Room)
                val localBooks: List<Book> = remote.map { it.toLocalBook() }

                // 3) Reemplazamos todo el catálogo local con lo nuevo
                localRepo.replaceAll(localBooks)

                // 4) Actualizamos el estado de UI con los DTO remotos
                _uiState.value = CatalogUiState(
                    isLoading = false,
                    error = null,
                    books = remote
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Error al cargar catálogo"
                )
            }
        }
    }
}

class CatalogViewModelFactory(
    private val repo: BookRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CatalogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CatalogViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
