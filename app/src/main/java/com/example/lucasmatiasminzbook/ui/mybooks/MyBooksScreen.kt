package com.example.lucasmatiasminzbook.ui.mybooks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lucasmatiasminzbook.data.remote.RetrofitClient
import com.example.lucasmatiasminzbook.data.remote.dto.CreateBookRequest
import com.example.lucasmatiasminzbook.data.remote.dto.BookDto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBooksScreen(
    onBack: () -> Unit,
    onOpenBook: (Long) -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Campos del formulario
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var pricePurchase by remember { mutableStateOf("") }
    var priceRent by remember { mutableStateOf("") }

    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    // Lista local solo para “Mis libros creados”
    val createdBooks = remember { mutableStateListOf<BookDto>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear/Ver Libros") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ===== FORMULARIO =====
            OutlinedTextField(
                value = title,
                onValueChange = { title = it; error = null; success = null },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = author,
                onValueChange = { author = it; error = null; success = null },
                label = { Text("Autor") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it; error = null; success = null },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = pricePurchase,
                onValueChange = { pricePurchase = it.filter { c -> c.isDigit() } },
                label = { Text("Precio compra (CLP)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = priceRent,
                onValueChange = { priceRent = it.filter { c -> c.isDigit() } },
                label = { Text("Precio arriendo 1 semana (CLP)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(4.dp))
            }
            if (success != null) {
                Text(success!!, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(4.dp))
            }

            Button(
                onClick = {
                    // Validaciones básicas
                    val t = title.trim()
                    val a = author.trim()
                    val d = description.trim()
                    val pCompra = pricePurchase.toDoubleOrNull()
                    // el precio de arriendo lo usas solo en la app local, el microservicio no lo necesita
                    if (t.isEmpty() || a.isEmpty() || d.isEmpty() || pCompra == null) {
                        error = "Completa título, autor, descripción y precio de compra"
                        success = null
                        return@Button
                    }

                    loading = true
                    error = null
                    success = null

                    scope.launch {
                        try {
                            // 1) Llamar al microservicio para CREAR el libro
                            val body = CreateBookRequest(
                                titulo = t,
                                autor = a,
                                categoria = "Personal",     // puedes cambiar la categoría
                                descripcion = d,
                                precio = pCompra,
                                stock = 1,
                                imagenUrl = null           // más adelante lo conectamos con imágenes
                            )

                            val created = RetrofitClient.catalogApi.createBook(body)

                            // 2) Agregar a la lista local de “Mis libros creados”
                            createdBooks.add(created)

                            success = "Libro creado correctamente"
                            // limpiamos formulario
                            title = ""
                            author = ""
                            description = ""
                            pricePurchase = ""
                            priceRent = ""

                            snackbarHostState.showSnackbar("Libro '${created.titulo}' creado")
                        } catch (e: Exception) {
                            error = e.message ?: "No se pudo crear el libro"
                        } finally {
                            loading = false
                        }
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (loading) "Guardando..." else "Guardar libro")
            }

            Spacer(Modifier.height(24.dp))

            // ===== LISTA DE LIBROS CREADOS EN ESTA SESIÓN =====
            Text("Mis libros creados", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (createdBooks.isEmpty()) {
                Text("Aún no has creado libros.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(createdBooks) { book ->
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            tonalElevation = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenBook(book.id) }
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(book.titulo, style = MaterialTheme.typography.titleMedium)
                                Text(book.autor, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
