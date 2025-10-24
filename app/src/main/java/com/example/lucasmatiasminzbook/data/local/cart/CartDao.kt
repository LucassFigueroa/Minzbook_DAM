package com.example.lucasmatiasminzbook.data.local.cart

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart")
    fun getAll(): Flow<List<CartItem>>

    @Insert
    suspend fun insert(item: CartItem): Long

    @Query("DELETE FROM cart WHERE id = :itemId")
    suspend fun delete(itemId: Long)

    @Query("DELETE FROM cart")
    suspend fun clear()
}
