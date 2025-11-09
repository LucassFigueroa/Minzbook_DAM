package com.example.lucasmatiasminzbook.data.local.purchase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {
    @Insert
    suspend fun insert(purchase: Purchase)

    @Query("SELECT * FROM purchases WHERE userId = :userId ORDER BY purchaseDate DESC")
    fun getPurchasesByUser(userId: Long): Flow<List<Purchase>>
}
