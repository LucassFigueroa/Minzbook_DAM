package com.example.lucasmatiasminzbook.ui.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lucasmatiasminzbook.data.local.cart.CartRepository
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CartRepository(application)

    val cartItems = repository.getCartItems()

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun deleteFromCart(itemId: Long) {
        viewModelScope.launch {
            repository.delete(itemId)
        }
    }
}
