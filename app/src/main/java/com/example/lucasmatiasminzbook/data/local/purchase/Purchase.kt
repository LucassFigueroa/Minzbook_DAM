package com.example.lucasmatiasminzbook.data.local.purchase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class Purchase(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val bookId: Long,
    val title: String,
    val type: String, // "Compra" o "Arriendo"
    val price: Int,
    val purchaseDate: Long = System.currentTimeMillis()
)
