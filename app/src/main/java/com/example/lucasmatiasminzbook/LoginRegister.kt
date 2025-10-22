package com.example.lucasmatiasminzbook.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onGoRegister: () -> Unit,
    // Devuelve null si todo bien; string con error si falla
    onTryLogin: (email: String, password: String) -> String?
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val emailValid by remember(email) { mutableStateOf(email.endsWith("@gmail.com")) }
    val passValid by remember(password) { mutableStateOf(password.length >= 7) }
    val canSubmit by remember(emailValid, passValid) { mutableStateOf(emailValid && passValid) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Inicia sesión", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trim(); error = null },
            label = { Text("Correo (solo @gmail.com)") },
            singleLine = true,
            isError = (email.isNotEmpty() && !emailValid) || (error != null),
            supportingText = {
                when {
                    email.isNotEmpty() && !emailValid ->
                        Text("Correo inválido (debe terminar en @gmail.com)")
                    error != null -> Text(error!!)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; error = null },
            label = { Text("Contraseña (mín. 7)") },
            singleLine = true,
            isError = (password.isNotEmpty() && !passValid) || (error != null),
            supportingText = {
                when {
                    password.isNotEmpty() && !passValid ->
                        Text("La contraseña debe tener al menos 7 caracteres")
                    error != null -> Text(error!!)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val resultError = onTryLogin(email, password)
                error = resultError // null si OK; mensaje si falla
            },
            enabled = canSubmit,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Entrar") }

        TextButton(onClick = onGoRegister) { Text("¿No tienes cuenta? Regístrate") }
        TextButton(onClick = onBack) { Text("Volver") }
    }
}

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onGoLogin: () -> Unit,
    // Devuelve null si todo bien; string con error si falla (por ejemplo cuenta ya existe)
    onTryRegister: (email: String, password: String) -> String?
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val emailValid by remember(email) { mutableStateOf(email.endsWith("@gmail.com")) }
    val passValid by remember(password) { mutableStateOf(password.length >= 7) }
    val canSubmit by remember(emailValid, passValid) { mutableStateOf(emailValid && passValid) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Crea tu cuenta", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trim(); error = null },
            label = { Text("Correo (solo @gmail.com)") },
            singleLine = true,
            isError = (email.isNotEmpty() && !emailValid) || (error != null),
            supportingText = {
                when {
                    email.isNotEmpty() && !emailValid ->
                        Text("Correo inválido (debe terminar en @gmail.com)")
                    error != null -> Text(error!!)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; error = null },
            label = { Text("Contraseña (mín. 7)") },
            singleLine = true,
            isError = (password.isNotEmpty() && !passValid) || (error != null),
            supportingText = {
                when {
                    password.isNotEmpty() && !passValid ->
                        Text("La contraseña debe tener al menos 7 caracteres")
                    error != null -> Text(error!!)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val resultError = onTryRegister(email, password)
                error = resultError
            },
            enabled = canSubmit,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Crear cuenta") }

        TextButton(onClick = onGoLogin) { Text("¿Ya tienes cuenta? Inicia sesión") }
        TextButton(onClick = onBack) { Text("Volver") }
    }
}
