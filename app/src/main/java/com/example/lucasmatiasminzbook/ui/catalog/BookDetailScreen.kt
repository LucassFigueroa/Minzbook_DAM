package com.example.lucasmatiasminzbook.ui.catalog

import androidx.compose.material3.Text

import android.util.Base64
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lucasmatiasminzbook.AuthLocalStore
import com.example.lucasmatiasminzbook.data.local.book.BookRepository
import com.example.lucasmatiasminzbook.data.local.book.Review
import com.example.lucasmatiasminzbook.data.local.user.Role
import com.example.lucasmatiasminzbook.data.local.user.UserRepository
import com.example.lucasmatiasminzbook.data.remote.RetrofitClient
import com.example.lucasmatiasminzbook.ui.cart.CartViewModel
import com.example.lucasmatiasminzbook.ui.common.StarDisplay
import com.example.lucasmatiasminzbook.ui.common.StarInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: Long,
    userId: Long?,
    onBack: () -> Unit,
    isAdmin: Boolean,
    cartViewModel: CartViewModel
) {
    val ctx = LocalContext.current
    val repo = remember(ctx.applicationContext) { BookRepository(ctx.applicationContext) }
    val userRepo = remember(ctx.applicationContext) { UserRepository(ctx.applicationContext) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val book by repo.book(bookId).collectAsState(initial = null)
    val avg by repo.averageForBook(bookId).collectAsState(initial = null)
    val reviews by repo.reviewsForBook(bookId).collectAsState(initial = emptyList())
    val user by userRepo.getLoggedInUserFlow().collectAsState(initial = null)

    var comment by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }
    var sending by remember { mutableStateOf(false) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<Any?>(null) }

    // ===== NUEVO: portada remota desde el micro (BLOB -> base64 -> ByteArray) =====
    var remoteCoverBytes by remember { mutableStateOf<ByteArray?>(null) }

    LaunchedEffect(bookId) {
        try {
            val remote = withContext(Dispatchers.IO) {
                RetrofitClient.catalogApi.getBookById(bookId)
            }
            val b64 = remote.portadaBase64
            if (!b64.isNullOrBlank()) {
                remoteCoverBytes = Base64.decode(b64, Base64.DEFAULT)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ============ Diálogo de eliminar ============
    if (showDeleteDialog) {
        var reason by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = {
                Column {
                    Text("Por favor, escribe el motivo de la eliminación.")
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Motivo") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (reason.isNotBlank()) {
                        scope.launch {
                            try {
                                when (val item = itemToDelete) {
                                    is Review -> repo.deleteReview(item)
                                    is Long -> {
                                        deleteBookFromMicroservice(item)
                                        onBack()
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                snackbarHostState.showSnackbar(
                                    e.localizedMessage ?: "Error al eliminar"
                                )
                            } finally {
                                showDeleteDialog = false
                            }
                        }
                    }
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detalle del libro") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (isAdmin) {
                        IconButton(onClick = {
                            itemToDelete = bookId
                            showDeleteDialog = true
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Eliminar libro")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (book == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val currency = remember {
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).apply {
                currency = Currency.getInstance("CLP")
                maximumFractionDigits = 0
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ===== Portada =====
            item {
                Surface(tonalElevation = 2.dp, shape = MaterialTheme.shapes.medium) {
                    val cover = book!!.coverUri?.trim()?.trim('"')
                    val blobBytes = remoteCoverBytes

                    when {
                        // 1) BLOB desde el microservicio
                        blobBytes != null -> {
                            AsyncImage(
                                model = ImageRequest.Builder(ctx)
                                    .data(blobBytes)
                                    .build(),
                                contentDescription = "Portada remota",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // 2) URI local (libro creado en el cel)
                        !cover.isNullOrBlank() -> {
                            AsyncImage(
                                model = cover,
                                contentDescription = "Portada local",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // 3) Nada → placeholder
                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Sin portada",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // ===== Título, autor y compra/arriendo =====
            item {
                Text(
                    book!!.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "de ${book!!.author}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = {
                        scope.launch {
                            cartViewModel.addToCart(book!!)
                            snackbarHostState.showSnackbar(
                                "'${book!!.title}' agregado al carrito"
                            )
                        }
                    }) {
                        Text("Compra: ${currency.format(book!!.purchasePrice)}")
                    }

                    Button(onClick = {
                        scope.launch {
                            cartViewModel.addToCart(book!!)
                            snackbarHostState.showSnackbar(
                                "'${book!!.title}' agregado para arriendo"
                            )
                        }
                    }) {
                        Text("Arriendo 1 semana: ${currency.format(book!!.rentPrice)}")
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val rounded = (avg ?: 0.0).toInt()
                    StarDisplay(rating = rounded)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (avg == null) "Sin calificaciones"
                        else String.format(Locale.US, "%.1f / 5", avg)
                    )
                }
            }

            // ===== Descripción =====
            item {
                Text(
                    "Descripción",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(book!!.description, style = MaterialTheme.typography.bodyMedium)
            }

            // ===== Reseñas =====
            item {
                Text(
                    "Reseñas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (reviews.isEmpty()) {
                item {
                    Text(
                        "Aún no hay reseñas. ¡Sé el/la primero/a!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(reviews, key = { it.createdAt }) { r ->
                    ReviewItem(
                        review = r,
                        canDelete = user?.role == Role.MODERATOR
                    ) {
                        itemToDelete = r
                        showDeleteDialog = true
                    }
                    HorizontalDivider()
                }
            }

            // ===== Tu reseña =====
            item {
                Text(
                    "Tu reseña",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))

                StarInput(rating = rating, onChange = { rating = it })
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Escribe tu reseña") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                error?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        scope.launch {
                            error = null

                            val storedEmail = AuthLocalStore.lastEmail(ctx) ?: ""
                            val storedName = AuthLocalStore.lastName(ctx) ?: ""

                            when {
                                rating !in 1..5 ->
                                    error = "Selecciona de 1 a 5 estrellas"

                                comment.isBlank() ->
                                    error = "Escribe un comentario"

                                (userId == null && storedEmail.isBlank()) ->
                                    error = "Debes iniciar sesión para calificar"

                                else -> {
                                    sending = true
                                    try {
                                        val effectiveEmail =
                                            if (storedEmail.isNotBlank()) storedEmail
                                            else "user${userId}@minzbook.local"

                                        val effectiveName =
                                            if (storedName.isNotBlank()) storedName
                                            else "Usuario"

                                        repo.addReview(
                                            bookId = bookId,
                                            userEmail = effectiveEmail,
                                            userName = effectiveName,
                                            rating = rating,
                                            comment = comment.trim()
                                        )

                                        rating = 0
                                        comment = ""

                                    } catch (e: Exception) {
                                        error = e.localizedMessage
                                            ?: "No se pudo guardar la reseña"
                                    } finally {
                                        sending = false
                                    }
                                }
                            }
                        }
                    },
                    enabled = !sending
                ) {
                    Text(if (sending) "Enviando..." else "Publicar reseña")
                }
            }
        }
    }
}

private suspend fun deleteBookFromMicroservice(bookId: Long) {
    withContext(Dispatchers.IO) {
        RetrofitClient.catalogApi.deleteBook(
            id = bookId,
            role = "ADMIN"
        )
    }
}

@Composable
private fun ReviewItem(
    review: Review,
    canDelete: Boolean,
    onDelete: () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = review.userName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            if (canDelete) {
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Eliminar reseña",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        StarDisplay(rating = review.rating)
        Spacer(Modifier.height(4.dp))
        Text(review.comment, style = MaterialTheme.typography.bodyMedium)
    }
}
