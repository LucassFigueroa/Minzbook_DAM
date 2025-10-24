package com.example.lucasmatiasminzbook.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private fun minzLightScheme() = lightColorScheme(
    primary = MinzGreen,
    onPrimary = Color.White,
    primaryContainer = MinzBeige2,
    onPrimaryContainer = MinzGreen,

    secondary = MinzBrown,
    onSecondary = Color.White,
    secondaryContainer = MinzBeige2,
    onSecondaryContainer = MinzBrown,

    tertiary = MinzOrange,
    onTertiary = Color.White,

    surface = MinzBeige,
    onSurface = MinzOnLight,
    background = MinzBeige,
    onBackground = MinzOnLight,

    surfaceVariant = MinzBeige2,
    onSurfaceVariant = MinzOnLight,

    outline = MinzGreen.copy(alpha = 0.25f),
)

private fun minzDarkScheme() = darkColorScheme(
    primary = MinzGreen,
    onPrimary = Color.White,
    secondary = MinzBrown,
    onSecondary = Color.White,
    tertiary = MinzOrange,
    onTertiary = Color.Black,

    surface = Color(0xFF1F1B18),
    onSurface = MinzOnDark,
    background = Color(0xFF191512),
    onBackground = MinzOnDark,
    surfaceVariant = Color(0xFF2A2622),
    onSurfaceVariant = MinzOnDark.copy(alpha = 0.85f),
    outline = Color(0xFF4B463F)
)

@Composable
fun MinzbookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) minzDarkScheme() else minzLightScheme()
    MaterialTheme(
        colorScheme = colors,
        shapes = MinzShapes,
        typography = Typography(), // deja tu Typography si quieres
        content = content
    )
}
