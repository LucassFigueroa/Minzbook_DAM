package com.example.lucasmatiasminzbook.ui.mybooks

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.collectAsState
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
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.lucasmatiasminzbook.AuthLocalStore
import com.example.lucasmatiasminzbook.data.local.book.BookRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBooksScreen(
    onBack: () -> Unit,
    onOpenBook: (Long) -> Unit
) {
    val ctx = LocalContext.current
    val repo = remember(ctx.applicationContext) { BookRepository(ctx.applicationContext) }
    val userEmail = AuthLocalStore.lastEmail(ctx) ?: ""
    val books by repo.booksByUser(userEmail).collectAsState(initial = emptyList())
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

    val cropImage = rememberLauncherForActivityResult(CropImageContract()) {
        if (it.isSuccessful) {
            coverUri = it.uriContent
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear/Ver Libros") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Crea un nuevo libro", style = MaterialTheme.typography.titleLarge)
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
                        cropImage.launch(
                            CropImageContractOptions(
                                uri = null,
                                cropImageOptions = CropImageOptions(imageSourceIncludeGallery = true, imageSourceIncludeCamera = true)
                            )
                        )
                    }
                ) {
                    Text(if (coverUri == null) "Elegir portada" else "Cambiar portada")
                }
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Autor") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                OutlinedTextField(
                    value = purchasePrice, onValueChange = { purchasePrice = it.filter(Char::isDigit) },
                    label = { Text("Precio compra (CLP)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = rentPrice, onValueChange = { rentPrice = it.filter(Char::isDigit) },
                    label = { Text("Precio arriendo 1 semana (CLP)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true, modifier = Modifier.fillMaxWidth()
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
                                            rentPrice = rent,
                                            creatorEmail = userEmail
                                        )
                                        title = ""; author = ""; description = ""
                                        purchasePrice = ""; rentPrice = ""; coverUri = null
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
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Text("Mis libros creados", style = MaterialTheme.typography.titleLarge)
            }
            items(books, key = { it.id }) { book ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenBook(book.id) }
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(book.title, style = MaterialTheme.typography.titleMedium)
                        Text(book.author, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
