@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.lucasmatiasminzbook.ui.catalog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lucasmatiasminzbook.data.Book

@Composable
fun CatalogScreen(
    vm: CatalogViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val books by vm.books.collectAsState()

    Column(Modifier.fillMaxSize()) {
        // AppBar simple (puedes reutilizar tu TopBar si prefieres)
        CenterAlignedTopAppBar(
            title = { Text("Explorar libros", fontWeight = FontWeight.SemiBold) }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(books, key = { it.id }) { book ->
                BookCard(book = book)
            }
        }
    }
}

@Composable
private fun BookCard(book: Book) {
    Surface(
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder de portada (cuadro con “Portada”)
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .then(
                        Modifier // borde suave usando outline del tema
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Box(
                        modifier = Modifier.size(72.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Portada", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(book.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(book.author, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(6.dp))
                Text(
                    book.synopsis,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
            }

            // (futuro) Botón de comprar/arriendo
            // TextButton(onClick = { }) { Text("Ver") }
        }
    }
}
