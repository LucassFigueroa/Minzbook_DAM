package com.example.lucasmatiasminzbook.data.local.book

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Long,
    val userEmail: String,
    val userName: String,
    val rating: Int,          // 1..5
    val comment: String,
    val createdAt: Long       // System.currentTimeMillis()
)
