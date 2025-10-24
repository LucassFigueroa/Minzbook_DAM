package com.example.lucasmatiasminzbook.data.local.cart

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Long,
    val title: String,
    val author: String,
    val price: Int,
    val type: String // "Compra" o "Arriendo"
)
