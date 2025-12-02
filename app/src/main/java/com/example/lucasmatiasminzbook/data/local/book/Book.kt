package com.example.lucasmatiasminzbook.data.local.book

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val title: String,
    val author: String,
    val description: String,

    // URI local de portada (solo para libros creados desde el celular)
    val coverUri: String? = null,

    // precios en CLP
    val purchasePrice: Int,
    val rentPrice: Int,

    // quién lo creó (opcional)
    val creatorEmail: String? = null
)
