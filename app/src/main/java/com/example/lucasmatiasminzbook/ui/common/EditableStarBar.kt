package com.example.lucasmatiasminzbook.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun EditableStarBar(value: Int, onValueChange: (Int) -> Unit) {
    Row {
        (1..5).forEach { i ->
            IconButton(onClick = { onValueChange(i) }) {
                Icon(
                    imageVector = if (i <= value) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Estrella $i",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
