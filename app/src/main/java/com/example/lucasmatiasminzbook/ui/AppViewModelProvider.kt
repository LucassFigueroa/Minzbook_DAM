package com.example.lucasmatiasminzbook.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.lucasmatiasminzbook.MinzbookApplication
import com.example.lucasmatiasminzbook.ui.checkout.CheckoutViewModel
import com.example.lucasmatiasminzbook.ui.profile.ProfileViewModel
import com.example.lucasmatiasminzbook.ui.support.SupportViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            SupportViewModel(minzbookApplication().container.ticketRepository)
        }
        initializer {
            CheckoutViewModel(minzbookApplication())
        }
        initializer {
            ProfileViewModel(minzbookApplication())
        }
    }
}

fun CreationExtras.minzbookApplication(): MinzbookApplication = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MinzbookApplication)
