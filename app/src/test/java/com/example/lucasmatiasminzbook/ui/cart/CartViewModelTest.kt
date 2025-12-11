package com.example.lucasmatiasminzbook.ui.cart

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.lucasmatiasminzbook.data.local.book.Book
import com.example.lucasmatiasminzbook.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CartViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var cartViewModel: CartViewModel

    @Before
    fun onBefore() {
        cartViewModel = CartViewModel()
    }

    private fun createDummyBook(id: Long): Book {
        return Book(
            id = id,
            title = "Title $id",
            author = "Author $id",
            description = "",
            coverUri = null,
            purchasePrice = 10,
            rentPrice = 5,
            creatorEmail = null
        )
    }

    @Test
    fun `initial state is an empty cart`() {
        Assert.assertTrue(cartViewModel.cartItems.value.isEmpty())
    }

    @Test
    fun `addToCart adds a book`() {
        val book = createDummyBook(1)

        cartViewModel.addToCart(book)

        Assert.assertEquals(1, cartViewModel.cartItems.value.size)
        Assert.assertEquals(book, cartViewModel.cartItems.value.first())
    }

    @Test
    fun `removeFromCart removes a book`() {
        val book = createDummyBook(1)
        cartViewModel.addToCart(book)

        cartViewModel.removeFromCart(book)

        Assert.assertTrue(cartViewModel.cartItems.value.isEmpty())
    }

    @Test
    fun `clearCart removes all items`() {
        val book1 = createDummyBook(1)
        val book2 = createDummyBook(2)
        cartViewModel.addToCart(book1)
        cartViewModel.addToCart(book2)

        cartViewModel.clearCart()

        Assert.assertTrue(cartViewModel.cartItems.value.isEmpty())
    }
}