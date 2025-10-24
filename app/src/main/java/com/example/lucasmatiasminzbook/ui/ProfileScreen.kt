package com.example.lucasmatiasminzbook.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.lucasmatiasminzbook.AuthLocalStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit
) {
    val ctx = LocalContext.current

    var name by remember { mutableStateOf(AuthLocalStore.lastName(ctx) ?: "Usuario") }
    var photo by remember { mutableStateOf(AuthLocalStore.profilePhotoUri(ctx)) } // <- ahora existe
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var info by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        photo = uri?.toString()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(shape = MaterialTheme.shapes.medium, tonalElevation = 2.dp) {
                Image(
                    painter = rememberAsyncImagePainter(model = photo?.let(Uri::parse)),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Button(onClick = {
                pickMedia.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }) { Text(if (photo == null) "Elegir foto" else "Cambiar foto") }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre visible") },
                modifier = Modifier.fillMaxWidth()
            )

            Divider()

            Text("Cambiar contraseña (opcional)")
            OutlinedTextField(
                value = oldPass, onValueChange = { oldPass = it },
                label = { Text("Contraseña actual") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            OutlinedTextField(
                value = newPass, onValueChange = { newPass = it },
                label = { Text("Nueva contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            OutlinedTextField(
                value = confirmPass, onValueChange = { confirmPass = it },
                label = { Text("Confirmar nueva contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)
            if (info != null) Text(info!!, color = MaterialTheme.colorScheme.primary)

            Button(
                onClick = {
                    error = null; info = null

                    // Guardar nombre y foto localmente
                    AuthLocalStore.setSession(
                        ctx,
                        AuthLocalStore.lastEmail(ctx) ?: "",
                        name
                    )
                    AuthLocalStore.setProfilePhotoUri(ctx, photo) // <- ahora existe

                    // Cambio de contraseña (simulado/local)
                    if (newPass.isNotBlank() || confirmPass.isNotBlank() || oldPass.isNotBlank()) {
                        if (newPass.length < 7) {
                            error = "La nueva contraseña debe tener al menos 7 caracteres"
                            return@Button
                        }
                        if (newPass != confirmPass) {
                            error = "Las contraseñas nuevas no coinciden"
                            return@Button
                        }
                        // TODO: validar oldPass contra backend/Room si corresponde
                        info = "Perfil actualizado (contraseña y datos)."
                    } else {
                        info = "Perfil actualizado."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar cambios")
            }
        }
    }
}
