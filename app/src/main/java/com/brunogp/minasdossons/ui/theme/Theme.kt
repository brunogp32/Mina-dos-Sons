package com.brunogp.minasdossons.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val colors =
    lightColorScheme(
        primary = Color(0xFF2F7D32),
        secondary = Color(0xFFD6A22A),
        tertiary = Color(0xFF4A90A4),
        background = Color(0xFF8BD3E6),
        surface = Color(0xFFFFF7D7),
        onPrimary = Color.White,
        onSecondary = Color(0xFF2C1F10),
        onBackground = Color(0xFF2C241B),
        onSurface = Color(0xFF2C241B),
    )

@Composable
fun MinasDosSonsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography,
        content = content,
    )
}
