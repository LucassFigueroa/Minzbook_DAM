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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingsScreen(
    onBack: () -> Unit,
    isAuthenticated: Boolean,
    userId: Long?
) {
    val vm: RatingsViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    // 1) si no está logeado, mostramos mensaje y listo (no crashea)
    if (!isAuthenticated) {
        NotLoggedInScreen(onBack)
        return
    }

    // 2) si userId viene null por alguna razón, tampoco crashea
    if (userId == null) {
        MissingUserIdScreen(onBack)
        return
    }

    // 3) cargamos reseñas sólo cuando tenemos userId válido
    LaunchedEffect(userId) {
        vm.loadUserRatings(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis notas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator()
                }

                state.errorMessage != null -> {
                    Text(text = state.errorMessage ?: "Error desconocido")
                }

                state.reviews.isEmpty() -> {
                    Text("Todavía no has publicado reseñas.")
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.reviews) { review ->
                            Card {
                                Column(Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Libro ID: ${review.bookId}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text("⭐ ${review.rating}/5")
                                    Spacer(Modifier.height(4.dp))
                                    Text(review.comment)
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "Fecha: ${review.fecha}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotLoggedInScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Debes iniciar sesión para ver tus notas.")
        Spacer(Modifier.height(8.dp))
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}

@Composable
private fun MissingUserIdScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("No se pudo identificar tu usuario. Vuelve a iniciar sesión.")
        Spacer(Modifier.height(8.dp))
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}
