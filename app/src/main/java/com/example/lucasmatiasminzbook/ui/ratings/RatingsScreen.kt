package com.example.lucasmatiasminzbook.ui.ratings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lucasmatiasminzbook.ui.AppViewModelProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingsScreen(
    onBack: () -> Unit,
    isAuthenticated: Boolean,
    userId: Long?,
    viewModel: RatingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsState()

    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Mostrar mensaje cuando se envía review
    LaunchedEffect(state.reviewSent) {
        if (state.reviewSent) {
            scope.launch {
                snackbarHostState.showSnackbar("Reseña enviada correctamente")
            }
            viewModel.clearReviewSentFlag()
            rating = 0
            comment = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notas y reseñas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            // 🔒 Bloqueo si NO hay sesión válida
            if (!isAuthenticated || userId == null) {
                Text(
                    text = "Debes iniciar sesión para publicar una reseña.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Vuelve a la pantalla de inicio, ingresa con tu cuenta y luego entra de nuevo a esta sección.",
                    style = MaterialTheme.typography.bodyMedium
                )
                return@Column
            }

            // ===============================
            // FORMULARIO DE NUEVA RESEÑA
            // ===============================
            Text(
                text = "Publicar nueva reseña",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            // Rating simple 1–5 (puedes cambiarlo por estrellitas si quieres)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                (1..5).forEach { value ->
                    FilterChip(
                        selected = rating == value,
                        onClick = { rating = value },
                        label = { Text("$value ★") }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Comentario") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.createReview(
                        userId = userId,
                        rating = rating,
                        comment = comment
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading &&
                        rating in 1..5 &&
                        comment.isNotBlank()
            ) {
                Text("Publicar reseña")
            }

            Spacer(Modifier.height(24.dp))

            // ===============================
            // LISTA DE RESEÑAS EXISTENTES
            // ===============================
            Text(
                text = "Mis reseñas",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            if (state.isLoading && state.reviews.isEmpty()) {
                CircularProgressIndicator()
            } else {
                LazyColumn {
                    items(state.reviews) { review ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    text = "${review.rating} ★",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = review.comment,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            // ===============================
            // ERRORES
            // ===============================
            state.error?.let { errorMsg ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = errorMsg,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
