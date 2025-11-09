package com.example.lucasmatiasminzbook.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lucasmatiasminzbook.data.local.purchase.Purchase
import com.example.lucasmatiasminzbook.data.local.purchase.PurchaseRepository
import com.example.lucasmatiasminzbook.data.local.user.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val purchaseRepository = PurchaseRepository(application)
    private val userRepository = UserRepository(application)

    val purchases: StateFlow<List<Purchase>> = userRepository.getLoggedInUserFlow()
        .flatMapLatest { user ->
            purchaseRepository.getPurchasesByUser(user?.id ?: -1)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
