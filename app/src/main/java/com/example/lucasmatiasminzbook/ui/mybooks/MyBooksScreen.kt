package com.example.lucasmatiasminzbook.ui.mybooks

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.lucasmatiasminzbook.data.local.book.BookRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBooksScreen(
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val repo = remember { BookRepository(ctx) }
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var purchasePrice by remember { mutableStateOf("") }
    var rentPrice by remember { mutableStateOf("") }
    var coverUri by remember { mutableStateOf<Uri?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }

    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) coverUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis libros") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (coverUri != null) {
                Surface(shape = MaterialTheme.shapes.medium, tonalElevation = 2.dp) {
                    Image(
                        painter = rememberAsyncImagePainter(coverUri),
                        contentDescription = "Portada seleccionada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            OutlinedButton(
                onClick = {
                    pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            ) {
                Text(if (coverUri == null) "Elegir portada" else "Cambiar portada")
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                label = { Text("Autor") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = purchasePrice,
                onValueChange = { purchasePrice = it.filter(Char::isDigit) },
                label = { Text("Precio compra (CLP)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = rentPrice,
                onValueChange = { rentPrice = it.filter(Char::isDigit) },
                label = { Text("Precio arriendo 1 semana (CLP)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (message != null) {
                val color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                Text(message!!, color = color)
            }

            Button(
                onClick = {
                    message = null
                    isError = false
                    val purchase = purchasePrice.toIntOrNull()
                    val rent = rentPrice.toIntOrNull()

                    when {
                        title.isBlank() -> {
                            message = "Ingresa un título"
                            isError = true
                        }
                        author.isBlank() -> {
                            message = "Ingresa un autor"
                            isError = true
                        }
                        description.isBlank() -> {
                            message = "Ingresa una descripción"
                            isError = true
                        }
                        purchase == null || purchase <= 0 -> {
                            message = "Precio de compra inválido"
                            isError = true
                        }
                        rent == null || rent <= 0 -> {
                            message = "Precio de arriendo inválido"
                            isError = true
                        }
                        else -> {
                            scope.launch {
                                saving = true
                                try {
                                    repo.addBook(
                                        title = title.trim(),
                                        author = author.trim(),
                                        description = description.trim(),
                                        coverUri = coverUri?.toString(),
                                        purchasePrice = purchase,
                                        rentPrice = rent
                                    )
                                    title = ""
                                    author = ""
                                    description = ""
                                    purchasePrice = ""
                                    rentPrice = ""
                                    coverUri = null
                                    message = "Libro creado correctamente"
                                } catch (e: Exception) {
                                    message = e.localizedMessage ?: "No se pudo crear el libro"
                                    isError = true
                                } finally {
                                    saving = false
                                }
                            }
                        }
                    }
                },
                enabled = !saving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (saving) "Guardando..." else "Guardar libro")
            }
        }
    }
}
