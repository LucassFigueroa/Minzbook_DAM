package com.example.lucasmatiasminzbook.data.local.purchase

import android.content.Context
import com.example.lucasmatiasminzbook.data.local.database.AppDatabase
import kotlinx.coroutines.flow.Flow

class PurchaseRepository(context: Context) {
    private val purchaseDao = AppDatabase.getInstance(context).purchaseDao()

    fun getPurchasesByUser(userId: Long): Flow<List<Purchase>> {
        return purchaseDao.getPurchasesByUser(userId)
    }

    suspend fun addPurchase(purchase: Purchase) {
        purchaseDao.insert(purchase)
    }
}
