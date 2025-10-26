package com.example.lucasmatiasminzbook.ui.ratings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.unit.dp
import com.example.lucasmatiasminzbook.AuthLocalStore
import com.example.lucasmatiasminzbook.data.local.book.BookRepository
import com.example.lucasmatiasminzbook.data.local.book.Review

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingsScreen(
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val appCtx = remember { ctx.applicationContext }
    val repo = remember(appCtx) { BookRepository(appCtx) }

    val email = AuthLocalStore.lastEmail(appCtx) ?: ""
    val reviews by repo.reviewsForUser(email).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calificaciones") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (reviews.isEmpty()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Aún no tienes calificaciones", style = MaterialTheme.typography.titleMedium)
                Text("Explora un libro y deja tu reseña ⭐️")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reviews, key = { it.id }) { r ->
                    ReviewCard(r)
                }
            }
        }
    }
}

@Composable
private fun ReviewCard(r: Review) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Libro #${r.bookId}", style = MaterialTheme.typography.labelLarge)
            // cantidad exacta de estrellas
            com.example.lucasmatiasminzbook.ui.common.StarDisplay(rating = r.rating, size = 20.dp)
            if (!r.comment.isNullOrBlank()) {
                Text(r.comment!!, style = MaterialTheme.typography.bodyMedium)
            }
            if (!r.userName.isNullOrBlank()) {
                Text(
                    "por ${r.userName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}