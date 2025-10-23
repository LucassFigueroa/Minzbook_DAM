package com.example.lucasmatiasminzbook.data.local.book

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val author: String,
    val description: String,
    val coverUri: String?,      // URI de la portada (galería/cámara)
    val purchasePrice: Int,     // precio compra (ej: 12000)
    val rentPrice: Int          // precio arriendo semana (ej: 6800)
)
