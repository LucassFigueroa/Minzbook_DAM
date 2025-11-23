package com.example.lucasmatiasminzbook.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lucasmatiasminzbook.data.local.book.Book
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBack: () -> Unit,
    onCheckout: () -> Unit,
    cartViewModel: CartViewModel
) {
    val cartItems by cartViewModel.cartItems.collectAsState()

    val total = cartItems.sumOf { it.purchasePrice }

    val currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).apply {
        currency = Currency.getInstance("CLP")
        maximumFractionDigits = 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (cartItems.isEmpty()) {
                Text("El carrito está vacío.", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(cartItems) { book ->
                        CartItemRow(
                            book = book,
                            currency = currency,
                            onRemove = { cartViewModel.removeFromCart(book) }
                        )
                        HorizontalDivider()
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Total: ${currency.format(total)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.End)
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onCheckout,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = cartItems.isNotEmpty()
                ) {
                    Text("Proceder al Pago")
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    book: Book,
    currency: NumberFormat,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(book.title, fontWeight = FontWeight.Bold)
            Text(
                "${book.author} - ${currency.format(book.purchasePrice)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        IconButton(onClick = onRemove) {
            Icon(Icons.Filled.Delete, contentDescription = "Quitar")
        }
    }
}
