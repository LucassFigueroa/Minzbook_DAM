package com.example.lucasmatiasminzbook.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val Gold = Color(0xFFFFB300)

/** SOLO visual: muestra N estrellas llenas de un total de 5. */
@Composable
fun StarDisplay(
    rating: Int,
    modifier: Modifier = Modifier,
    max: Int = 5,
    size: Dp = 18.dp,
    filledColor: Color = Gold,
    emptyColor: Color = Color.LightGray
) {
    Row(modifier) {
        for (i in 1..max) {
            val filled = i <= rating
            Icon(
                imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (filled) filledColor else emptyColor,
                modifier = Modifier.size(size)
            )
        }
    }
}

/** Interactiva: permite elegir 1..5 y actualiza onChange; útil al crear reseña. */
@Composable
fun StarInput(
    rating: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    max: Int = 5,
    size: Dp = 28.dp,
    filledColor: Color = Gold,
    emptyColor: Color = Color.LightGray
) {
    Row(modifier) {
        for (i in 1..max) {
            val filled = i <= rating
            Icon(
                imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Elegir $i estrellas",
                tint = if (filled) filledColor else emptyColor,
                modifier = Modifier
                    .size(size)
                    .clickable { onChange(i) }
            )
        }
    }
}
