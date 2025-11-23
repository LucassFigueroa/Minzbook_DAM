package com.example.lucasmatiasminzbook.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lucasmatiasminzbook.data.local.book.BookRepository
import com.example.lucasmatiasminzbook.data.mappers.toLocalBook
import com.example.lucasmatiasminzbook.data.remote.dto.BookDto
import com.example.lucasmatiasminzbook.data.remote.repository.CatalogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CatalogUiState(
    val isLoading: Boolean = false,
    val books: List<BookDto> = emptyList(),
    val error: String? = null
)

class CatalogViewModel(
    private val remoteRepo: CatalogRepository = CatalogRepository(),
    private val localRepo: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState

    fun loadBooks() {
        viewModelScope.launch {
            _uiState.value = CatalogUiState(isLoading = true)

            val result = remoteRepo.getBooks()
            result.fold(
                onSuccess = { remoteBooks ->
                    // 1) actualizamos UI
                    _uiState.value = CatalogUiState(
                        isLoading = false,
                        books = remoteBooks,
                        error = null
                    )

                    // 2) sincronizamos con Room
                    val localBooks = remoteBooks.map { it.toLocalBook() }
                    localRepo.replaceAll(localBooks)
                },
                onFailure = { err ->
                    _uiState.value = CatalogUiState(
                        isLoading = false,
                        books = emptyList(),
                        error = err.message ?: "Error al cargar libros"
                    )
                }
            )
        }
    }
}

class CatalogViewModelFactory(
    private val localRepo: BookRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CatalogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CatalogViewModel(localRepo = localRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
