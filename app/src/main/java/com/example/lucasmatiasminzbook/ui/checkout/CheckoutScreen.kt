package com.example.lucasmatiasminzbook.ui.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

// --- Transformaciones Visuales para el Formato Automático ---

class CreditCardVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Máximo 16 dígitos
        val trimmed = if (text.text.length >= 16) text.text.substring(0..15) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            // Inserta "-" después de cada bloque de 4, menos al final
            if (i % 4 == 3 && i != 15) out += "-"
        }

        val creditCardOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // 4-4-4-4 => agrega 1, 2 o 3 por los guiones
                if (offset <= 3) return offset
                if (offset <= 7) return offset + 1
                if (offset <= 11) return offset + 2
                if (offset <= 16) return offset + 3
                return 19
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 4) return offset
                if (offset <= 9) return offset - 1
                if (offset <= 14) return offset - 2
                if (offset <= 19) return offset - 3
                return 16
            }
        }

        return TransformedText(AnnotatedString(out), creditCardOffsetTranslator)
    }
}

class ExpiryDateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Máximo 4 dígitos: MMYY
        val trimmed = if (text.text.length >= 4) text.text.substring(0..3) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            // Inserta "/" después de los 2 primeros (MM/YY)
            if (i == 1) out += "/"
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // Antes del "/", igual; después, suma 1
                if (offset <= 1) return offset
                if (offset <= 4) return offset + 1
                return 5
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                return 4
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Diálogo de error
    if (showErrorDialog != null) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = null },
            title = { Text("Error de Validación") },
            text = { Text(showErrorDialog!!) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = null }) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Diálogo de éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* No cerrar tocando fuera */ },
            title = { Text("¡Compra Exitosa!") },
            text = { Text("Tu pedido ha sido procesado.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        onPaymentSuccess()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pago") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Introduce los datos de tu tarjeta", style = MaterialTheme.typography.titleLarge)

            // Número de tarjeta con guiones automáticos y máximo 16 dígitos
            OutlinedTextField(
                value = cardNumber,
                onValueChange = { input ->
                    val onlyDigits = input.filter(Char::isDigit)
                    if (onlyDigits.length <= 16) {
                        cardNumber = onlyDigits
                    }
                },
                label = { Text("Número de tarjeta") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = CreditCardVisualTransformation(),
                singleLine = true
            )

            // Fecha de expiración MMYY → se muestra como MM/YY
            OutlinedTextField(
                value = expiryDate,
                onValueChange = { input ->
                    val onlyDigits = input.filter(Char::isDigit)
                    if (onlyDigits.length <= 4) {
                        expiryDate = onlyDigits
                    }
                },
                label = { Text("Fecha de expiración (MM/AA)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = ExpiryDateVisualTransformation(),
                singleLine = true
            )

            // CVV solo 3 dígitos
            OutlinedTextField(
                value = cvv,
                onValueChange = { input ->
                    val onlyDigits = input.filter(Char::isDigit)
                    if (onlyDigits.length <= 3) {
                        cvv = onlyDigits
                    }
                },
                label = { Text("CVV") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = cardHolder,
                onValueChange = { cardHolder = it },
                label = { Text("Nombre del titular") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    val month = expiryDate.take(2).toIntOrNull()
                    val year = expiryDate.drop(2).toIntOrNull()

                    when {
                        cardNumber.length != 16 ->
                            showErrorDialog = "El número de tarjeta debe tener 16 dígitos."

                        cvv.length != 3 ->
                            showErrorDialog = "El CVV debe tener 3 dígitos."

                        expiryDate.length != 4 || month == null || month !in 1..12 || year == null ->
                            showErrorDialog = "La fecha de expiración no es válida. Usa formato MMYY."

                        cardHolder.isBlank() ->
                            showErrorDialog = "El nombre del titular no puede estar vacío."

                        else -> {
                            // Si todo está OK, mostramos la confirmación
                            showSuccessDialog = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pagar")
            }
        }
    }
}
