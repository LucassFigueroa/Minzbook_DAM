package com.example.lucasmatiasminzbook.ui.catalog

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.lucasmatiasminzbook.data.local.book.BookRepository
import com.example.lucasmatiasminzbook.data.remote.api.CatalogApi
import com.example.lucasmatiasminzbook.data.remote.dto.BookDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CatalogViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var catalogViewModel: CatalogViewModel
    private val bookRepository: BookRepository = mockk(relaxed = true)
    private val catalogApi: CatalogApi = mockk()

    @Before
    fun onBefore() {
        Dispatchers.setMain(testDispatcher)
        catalogViewModel = CatalogViewModel(bookRepository, catalogApi)
    }

    @After
    fun onAfter() {
        Dispatchers.resetMain()
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
        advanceUntilIdle()

        // Then
        val uiState = catalogViewModel.uiState.value
        Assert.assertFalse(uiState.isLoading)
        Assert.assertNull(uiState.error)
        Assert.assertEquals(fakeBooks, uiState.books)
    }

    @Test
    fun `when loading books fails, error is updated`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { catalogApi.getAllBooks() } throws Exception(errorMessage)

        // When
        catalogViewModel.loadBooks()
        advanceUntilIdle()

        // Then
        val uiState = catalogViewModel.uiState.value
        Assert.assertFalse(uiState.isLoading)
        Assert.assertEquals(errorMessage, uiState.error)
    }
}
