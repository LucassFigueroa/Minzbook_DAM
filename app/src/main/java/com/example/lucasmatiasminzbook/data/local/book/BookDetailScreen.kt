package com.example.lucasmatiasminzbook.data.local.book

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.lucasmatiasminzbook.AuthLocalStore
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: Long,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val repo = remember { BookRepository(ctx) }
    val scope = rememberCoroutineScope()

    val book by repo.book(bookId).collectAsState(initial = null)
    val avg by repo.averageForBook(bookId).collectAsState(initial = null)
    val reviews by repo.reviewsForBook(bookId).collectAsState(initial = emptyList())

    var comment by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }
    var sending by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del libro") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (book == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val currency = NumberFormat.getCurrencyInstance(Locale("es", "CL")).apply {
            currency = Currency.getInstance("CLP")
            maximumFractionDigits = 0
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Portada (placeholder si no hay)
                Surface(tonalElevation = 2.dp, shape = MaterialTheme.shapes.medium) {
                    Image(
                        painter = rememberAsyncImagePainter(model = book!!.coverUri?.let { Uri.parse(it) }),
                        contentDescription = "Portada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            item {
                Text(book!!.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                Text("de ${book!!.author}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AssistChip(onClick = {}, label = { Text("Compra: ${currency.format(book!!.purchasePrice)}") })
                    AssistChip(onClick = {}, label = { Text("Arriendo 1 semana: ${currency.format(book!!.rentPrice)}") })
                }

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingStars(avg ?: 0.0)
                    Spacer(Modifier.width(8.dp))
                    Text(if (avg == null) "Sin calificaciones" else String.format(Locale.US, "%.1f / 5", avg))
                }
            }

            item {
                Text("Descripción", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(book!!.description, style = MaterialTheme.typography.bodyMedium)
            }

            item { Text("Reseñas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }

            if (reviews.isEmpty()) {
                item { Text("Aún no hay reseñas. ¡Sé el/la primero/a!", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                items(reviews) { r -> ReviewItem(r); Divider() }
            }

            // Nueva reseña
            item {
                Text("Tu reseña", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))

                EditableStarBar(current = rating, onChange = { rating = it })
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Escribe tu reseña") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)

                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        scope.launch {
                            error = null
                            if (rating !in 1..5) { error = "Selecciona de 1 a 5 estrellas"; return@launch }
                            if (comment.isBlank()) { error = "Escribe un comentario"; return@launch }
                            val email = AuthLocalStore.lastEmail(ctx)
                            val name = AuthLocalStore.lastName(ctx) ?: "Usuario"
                            if (email == null) { error = "Debes iniciar sesión para calificar"; return@launch }
                            sending = true
                            try {
                                repo.addReview(bookId, email, name, rating, comment.trim())
                                rating = 0; comment = ""
                            } catch (e: Exception) {
                                error = e.localizedMessage ?: "No se pudo guardar la reseña"
                            } finally { sending = false }
                        }
                    },
                    enabled = !sending
                ) { Text(if (sending) "Enviando..." else "Publicar reseña") }
            }
        }
    }
}

@Composable
private fun ReviewItem(r: Review) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(r.userName, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.width(8.dp))
            RatingStars(r.rating.toDouble())
        }
        Spacer(Modifier.height(4.dp))
        Text(r.comment)
    }
}

@Composable
fun RatingStars(rating: Double, max: Int = 5) {
    Row {
        val full = rating.toInt()
        val hasHalf = (rating - full) >= 0.5
        repeat(full) { Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
        if (hasHalf) Icon(Icons.Filled.StarHalf, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        repeat(max - full - if (hasHalf) 1 else 0) {
            Icon(Icons.Outlined.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun EditableStarBar(current: Int, max: Int = 5, onChange: (Int) -> Unit) {
    Row {
        (1..max).forEach { i ->
            IconButton(onClick = { onChange(i) }) {
                if (i <= current) Icon(Icons.Filled.Star, contentDescription = "Star $i", tint = MaterialTheme.colorScheme.primary)
                else Icon(Icons.Outlined.Star, contentDescription = "Star $i", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
