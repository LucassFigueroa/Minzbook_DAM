package com.example.lucasmatiasminzbook.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.lucasmatiasminzbook.MinzbookApplication
import com.example.lucasmatiasminzbook.data.remote.RetrofitClient
import com.example.lucasmatiasminzbook.data.remote.repository.SupportRepository
import com.example.lucasmatiasminzbook.ui.checkout.CheckoutViewModel
import com.example.lucasmatiasminzbook.ui.profile.ProfileViewModel
import com.example.lucasmatiasminzbook.ui.support.SupportChatViewModel
import com.example.lucasmatiasminzbook.ui.support.SupportViewModel

object AppViewModelProvider {

    val Factory = viewModelFactory {

        // 👉 SupportViewModel ahora usa el microservicio remoto (SupportRepository)
        initializer {
            val supportRepository = SupportRepository(RetrofitClient.supportApi)
            SupportViewModel(supportRepository)
        }

        initializer {
            CheckoutViewModel(minzbookApplication())
        }

        initializer {
            ProfileViewModel(minzbookApplication())
        }

        initializer {
            val app = minzbookApplication()
            val supportRepo = com.example.lucasmatiasminzbook.data.remote.repository.SupportRepository(
                com.example.lucasmatiasminzbook.data.remote.RetrofitClient.supportApi
            )
            SupportChatViewModel(supportRepo)
        }

    }
}

fun CreationExtras.minzbookApplication(): MinzbookApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
            as MinzbookApplication)
