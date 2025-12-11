package com.example.lucasmatiasminzbook.ui.profile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lucasmatiasminzbook.data.local.purchase.Purchase
import com.example.lucasmatiasminzbook.data.local.purchase.PurchaseRepository
import com.example.lucasmatiasminzbook.data.local.user.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(
    application: Application,
    private val purchaseRepository: PurchaseRepository,
    private val userRepository: UserRepository
) : AndroidViewModel(application) {

    val purchases: StateFlow<List<Purchase>> = userRepository.getLoggedInUserFlow()
        .flatMapLatest { user ->
            purchaseRepository.getPurchasesByUser(user?.id ?: -1)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}

class ProfileViewModelFactory(
    private val application: Application,
    private val purchaseRepository: PurchaseRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(application, purchaseRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
