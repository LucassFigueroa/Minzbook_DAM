package com.example.lucasmatiasminzbook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onGoRegister: () -> Unit,
    // Devuelve null si OK; string con error si falla. NO navega.
    onTryLogin: suspend (email: String, password: String) -> String?,
    // El Activity te pasa esta lambda que lanza el prompt biométrico.
    onBiometricClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val emailValid by remember(email) { mutableStateOf(email.endsWith("@gmail.com")) }
    val passValid by remember(password) { mutableStateOf(password.length >= 7) }
    val canSubmit by remember(emailValid, passValid, loading) {
        mutableStateOf(emailValid && passValid && !loading)
    }

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
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
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
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                TextButton(onClick = { showPassword = !showPassword }) {
                    Text(if (showPassword) "Ocultar" else "Ver")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Al validar credenciales, se dispara el prompt biométrico inmediatamente.
        Button(
            onClick = {
                scope.launch {
                    loading = true
                    val resultError = onTryLogin(email, password)
                    loading = false
                    if (resultError == null) {
                        // Credenciales OK → mostrar prompt de huella inmediatamente
                        onBiometricClick()
                    } else {
                        error = resultError
                    }
                }
            },
            enabled = canSubmit,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (loading) "Entrando..." else "Entrar") }

        TextButton(onClick = onGoRegister) { Text("¿No tienes cuenta? Regístrate") }
        TextButton(onClick = onBack) { Text("Volver") }
    }
}

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onGoLogin: () -> Unit,
    // Devuelve null si OK; string con error si falla
    onTryRegister: suspend (email: String, password: String) -> String?,
    // Si el registro fue correcto, volver al Login
    onRegistered: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val emailValid by remember(email) { mutableStateOf(email.endsWith("@gmail.com")) }
    val passValid by remember(password) { mutableStateOf(password.length >= 7) }
    val canSubmit by remember(emailValid, passValid, loading) {
        mutableStateOf(emailValid && passValid && !loading)
    }

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
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
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
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                TextButton(onClick = { showPassword = !showPassword }) {
                    Text(if (showPassword) "Ocultar" else "Ver")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                scope.launch {
                    loading = true
                    val resultError = onTryRegister(email, password)
                    loading = false
                    if (resultError == null) {
                        // Registro OK → ir a Login
                        onRegistered()
                    } else {
                        error = resultError
                    }
                }
            },
            enabled = canSubmit,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (loading) "Creando..." else "Crear cuenta") }

        TextButton(onClick = onGoLogin) { Text("¿Ya tienes cuenta? Inicia sesión") }
        TextButton(onClick = onBack) { Text("Volver") }
    }
}
