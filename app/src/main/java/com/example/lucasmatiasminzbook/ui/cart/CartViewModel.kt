package com.example.lucasmatiasminzbook.ui.cart

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.lucasmatiasminzbook.data.local.book.Book

class CartViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow<List<Book>>(emptyList())
    val cartItems: StateFlow<List<Book>> = _cartItems

    fun addToCart(book: Book) {
        _cartItems.value = _cartItems.value + book
    }

    fun removeFromCart(book: Book) {
        _cartItems.value = _cartItems.value - book
    }

    // 🧹 Vaciar carrito después de la compra
    fun clearCart() {
        _cartItems.value = emptyList()
    }
}
