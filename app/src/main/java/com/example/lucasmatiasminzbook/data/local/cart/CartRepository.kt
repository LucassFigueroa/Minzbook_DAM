package com.example.lucasmatiasminzbook.data.local.cart

import android.content.Context
import com.example.lucasmatiasminzbook.data.local.database.AppDatabase
import kotlinx.coroutines.flow.Flow

class CartRepository(context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val cartDao = db.cartDao()

    fun getCartItems(): Flow<List<CartItem>> = cartDao.getAll()

    suspend fun addToCart(bookId: Long, title: String, author: String, price: Int, type: String) {
        val item = CartItem(
            bookId = bookId,
            title = title,
            author = author,
            price = price,
            type = type
        )
        cartDao.insert(item)
    }

    suspend fun clearCart() {
        cartDao.clear()
    }
    suspend fun delete(itemId: Long) {
        cartDao.delete(itemId)
    }
}
