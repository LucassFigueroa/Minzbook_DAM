package com.example.lucasmatiasminzbook.ui.catalog

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.lucasmatiasminzbook.data.local.book.BookRepository
import com.example.lucasmatiasminzbook.data.remote.api.CatalogApi
import com.example.lucasmatiasminzbook.data.remote.dto.BookDto
import com.example.lucasmatiasminzbook.util.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CatalogViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var catalogViewModel: CatalogViewModel
    private val bookRepository: BookRepository = mockk(relaxed = true)
    private val catalogApi: CatalogApi = mockk()

    @Before
    fun onBefore() {
        catalogViewModel = CatalogViewModel(bookRepository, catalogApi)
    }

    @Test
    fun `when books are loaded successfully, the ui state is updated`() = runTest {
        // Given
        val fakeBooks = listOf(
            BookDto(
                1,
                "Title",
                "Author",
                "Description",
                10.0,
                5,
                "Category",
                true,
                null,
                null
            )
        )
        coEvery { catalogApi.getAllBooks() } returns fakeBooks

        // When
        catalogViewModel.loadBooks()

        // Then
        val uiState = catalogViewModel.uiState.value
        assert(!uiState.isLoading)
        assert(uiState.error == null)
        Assert.assertEquals(fakeBooks, uiState.books)
    }

    @Test
    fun `when loading books fails, error is updated`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { catalogApi.getAllBooks() } throws Exception(errorMessage)

        // When
        catalogViewModel.loadBooks()

        // Then
        val uiState = catalogViewModel.uiState.value
        assert(!uiState.isLoading)
        Assert.assertEquals(errorMessage, uiState.error)
    }
}