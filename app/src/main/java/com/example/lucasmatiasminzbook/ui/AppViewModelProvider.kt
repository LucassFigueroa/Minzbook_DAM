package com.example.lucasmatiasminzbook.ui

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.lucasmatiasminzbook.viewmodel.AuthViewModel

/**
 * Fábrica centralizada de ViewModels para la app.
 *
 * Se usa así:
 * private val authViewModel: AuthViewModel by viewModels {
 *     AppViewModelProvider.Factory
 * }
 */
object AppViewModelProvider {

    val Factory = viewModelFactory {

        // 👉 AuthViewModel: como tu ViewModel tiene ctor sin parámetros,
        // simplemente lo instanciamos así:
        initializer {
            AuthViewModel()
        }

        // Si más adelante quieres agregar otros ViewModels con dependencias,
        // los vas sumando aquí con más `initializer { ... }`
        //
        // ejemplo:
        // initializer {
        //     SupportViewModel(
        //         supportRepository = (this[APPLICATION_KEY] as MinzbookApplication)
        //             .container.supportRepository
        //     )
        // }
    }
}
