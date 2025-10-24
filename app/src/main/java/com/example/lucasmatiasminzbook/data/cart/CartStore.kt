package com.example.lucasmatiasminzbook.data.cart

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class CartType { BUY, RENT }
data class CartItem(val bookId: Long, val type: CartType, val qty: Int = 1)

object CartStore {
    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items

    fun add(bookId: Long, type: CartType) {
        val cur = _items.value.toMutableList()
        val i = cur.indexOfFirst { it.bookId == bookId && it.type == type }
        if (i >= 0) cur[i] = cur[i].copy(qty = cur[i].qty + 1) else cur += CartItem(bookId, type, 1)
        _items.value = cur
    }

    fun remove(bookId: Long, type: CartType) {
        _items.value = _items.value.filterNot { it.bookId == bookId && it.type == type }
    }

    fun clear() { _items.value = emptyList() }
}
