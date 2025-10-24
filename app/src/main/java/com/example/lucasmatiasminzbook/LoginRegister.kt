package com.example.lucasmatiasminzbook

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/* ============ LOGIN ============ */

@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onGoRegister: () -> Unit,
    // valida credenciales; devuelve null si OK o mensaje si falla
    onTryLogin: suspend (email: String, password: String) -> String?,
    // se llama SOLO si credenciales OK → aquí disparas el BiometricPrompt desde MainActivity
    onCredentialsOk: () -> Unit,
    rememberInitial: Boolean,
    onToggleRemember: (Boolean) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var loading by rememberSaveable { mutableStateOf(false) }
    var rememberMe by rememberSaveable { mutableStateOf(rememberInitial) }

    val scope = rememberCoroutineScope()

    val emailValid = email.endsWith("@gmail.com")
    val passValid = password.length >= 7
    val canSubmit = emailValid && passValid && !loading

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
                    email.isNotEmpty() && !emailValid -> Text("Correo inválido (debe terminar en @gmail.com)")
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
                    password.isNotEmpty() && !passValid -> Text("La contraseña debe tener al menos 7 caracteres")
                    error != null -> Text(error!!)
                }
            },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                val image = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                val desc = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña"
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(imageVector = image, contentDescription = desc)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Mantener sesión
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Checkbox(checked = rememberMe, onCheckedChange = {
                rememberMe = it
                onToggleRemember(it)
            })
            Text("Mantener sesión iniciada", modifier = Modifier.padding(top = 12.dp))
        }

        Button(
            onClick = {
                scope.launch {
                    loading = true
                    val err = onTryLogin(email, password)
                    loading = false
                    if (err == null) {
                        onCredentialsOk() // → aquí muestras el prompt biométrico (MainActivity)
                    } else {
                        error = err
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

/* ============ REGISTER ============ */

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onGoLogin: () -> Unit,
    onTryRegister: suspend (name: String, email: String, password: String, photoUri: String?) -> String?,
    onRegistered: () -> Unit
) {
    val ctx = LocalContext.current
    var name by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var pass2 by rememberSaveable { mutableStateOf("") }
    var photoUri by rememberSaveable { mutableStateOf<String?>(null) }

    var showPassword by rememberSaveable { mutableStateOf(false) }
    var showPassword2 by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var loading by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Galería (Photo Picker)
    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        photoUri = uri?.toString()
    }

    val emailValid = email.endsWith("@gmail.com")
    val passValid = password.length >= 7 && password == pass2
    val nameValid = name.isNotBlank() && lastName.isNotBlank()
    val canSubmit = emailValid && passValid && nameValid && !loading

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Crear cuenta", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Apellido") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trim(); error = null },
            label = { Text("Correo (solo @gmail.com)") },
            singleLine = true,
            isError = (email.isNotEmpty() && !emailValid) || (error != null),
            supportingText = {
                when {
                    email.isNotEmpty() && !emailValid -> Text("Correo inválido (debe terminar en @gmail.com)")
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
            isError = password.isNotEmpty() && password.length < 7,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                val desc = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña"
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(imageVector = image, contentDescription = desc)
                }
            }
        )
        OutlinedTextField(
            value = pass2,
            onValueChange = { pass2 = it; error = null },
            label = { Text("Repetir contraseña") },
            singleLine = true,
            isError = pass2.isNotEmpty() && pass2 != password,
            visualTransformation = if (showPassword2) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (showPassword2) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                val desc = if (showPassword2) "Ocultar contraseña" else "Mostrar contraseña"
                IconButton(onClick = { showPassword2 = !showPassword2 }) {
                    Icon(imageVector = image, contentDescription = desc)
                }
            }
        )

        // Foto
        Row(Modifier.fillMaxWidth()) {
            Button(onClick = {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) { Text(if (photoUri == null) "Elegir foto (galería)" else "Cambiar foto") }
        }

        if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)

        Button(
            onClick = {
                scope.launch {
                    loading = true
                    error = null
                    val displayName = "${name.trim()} ${lastName.trim()}".trim()
                    val err = onTryRegister(displayName, email, password, photoUri)
                    loading = false
                    if (err == null) {
                        onRegistered() // → vuelve a Login
                    } else {
                        error = err
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
