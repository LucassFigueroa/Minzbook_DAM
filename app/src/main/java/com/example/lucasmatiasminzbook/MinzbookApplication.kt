package com.example.lucasmatiasminzbook

import android.app.Application
import com.example.lucasmatiasminzbook.data.AppContainer
import com.example.lucasmatiasminzbook.data.AppDataContainer

class MinzbookApplication : Application() {

    // Contenedor de dependencias usado por AppViewModelProvider
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()

        // Inicializa tu contenedor de datos (repositorios, etc.)
        container = AppDataContainer(this)
    }
}
