package com.example.lucasmatiasminzbook.ui.checkout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lucasmatiasminzbook.data.local.cart.CartRepository
import com.example.lucasmatiasminzbook.data.local.purchase.Purchase
import com.example.lucasmatiasminzbook.data.local.purchase.PurchaseRepository
import com.example.lucasmatiasminzbook.data.local.user.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CheckoutViewModel(application: Application) : AndroidViewModel(application) {

    private val cartRepository = CartRepository(application)
    private val purchaseRepository = PurchaseRepository(application)
    private val userRepository = UserRepository(application)

    fun processPayment(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val user = userRepository.getLoggedInUser() ?: return@launch
            val cartItems = cartRepository.getCartItems().first()

            cartItems.forEach { item ->
                val purchase = Purchase(
                    userId = user.id,
                    bookId = item.bookId,
                    title = item.title,
                    type = item.type,
                    price = item.price
                )
                purchaseRepository.addPurchase(purchase)
            }

            cartRepository.clearCart()
            onSuccess()
        }
    }
}
