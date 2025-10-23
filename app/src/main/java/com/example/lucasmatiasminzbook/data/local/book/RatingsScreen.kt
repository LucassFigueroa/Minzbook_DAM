package com.example.lucasmatiasminzbook.ui.ratings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.lucasmatiasminzbook.AuthLocalStore
import com.example.lucasmatiasminzbook.data.local.book.BookRepository
import com.example.lucasmatiasminzbook.data.local.book.Review

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingsScreen(
    onBack: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val repo = remember { BookRepository(ctx) }
    val email = remember { AuthLocalStore.lastEmail(ctx) }
    val name = remember { AuthLocalStore.lastName(ctx) ?: "Tú" }

    // Si no hay sesión, mostramos vacío amigable
    if (email == null) {
        EmptyState(onBack = onBack, title = "Calificaciones", message = "Inicia sesión para ver tus reseñas.")
        return
    }

    // Cargamos las reseñas del usuario
    val myReviews by repo.reviewsForUser(email).collectAsState(initial = emptyList())
    // Opcional: podríamos enriquecer con títulos si hiciera falta (aquí usamos solo lo guardado en Review)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis calificaciones") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (myReviews.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No has escrito reseñas todavía.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(myReviews, key = { it.id }) { review ->
                    ReviewCard(review = review)
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun ReviewCard(review: Review) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Libro #${review.bookId}", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("★".repeat(review.rating.coerceIn(1,5)))
                if (review.rating < 5) Text("☆".repeat(5-review.rating.coerceIn(0,5)))
            }
            if (review.comment.isNotBlank()) {
                Text(
                    text = review.comment,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Text("Sin comentario", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                text = "por ${review.userName} (${review.userEmail})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmptyState(onBack: () -> Unit, title: String, message: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text(message)
        }
    }
}
