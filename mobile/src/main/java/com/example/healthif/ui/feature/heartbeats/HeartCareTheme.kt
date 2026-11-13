package com.example.healthif.ui.feature.heartbeats

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

private val HeartCareLightColors = lightColorScheme(
    primary = Color(0xFFA9425D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDDE5),
    onPrimaryContainer = Color(0xFF48202C),
    secondary = Color(0xFF845461),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF9DEE5),
    onSecondaryContainer = Color(0xFF35212A),
    background = Color(0xFFFFF8F9),
    onBackground = Color(0xFF302329),
    surface = Color(0xFFFFF8F9),
    onSurface = Color(0xFF302329),
    surfaceVariant = Color(0xFFF3E1E6),
    onSurfaceVariant = Color(0xFF72535D),
    outline = Color(0xFF9B7C86),
    outlineVariant = Color(0xFFE3C6CF)
)

private val HeartCareDarkColors = darkColorScheme(
    primary = Color(0xFFFFAFC2),
    onPrimary = Color(0xFF641E34),
    primaryContainer = Color(0xFF633344),
    onPrimaryContainer = Color(0xFFFFDDE5),
    secondary = Color(0xFFE8BAC7),
    onSecondary = Color(0xFF482A35),
    secondaryContainer = Color(0xFF60404C),
    onSecondaryContainer = Color(0xFFF9DEE5),
    background = Color(0xFF21191D),
    onBackground = Color(0xFFF4E2E8),
    surface = Color(0xFF21191D),
    onSurface = Color(0xFFF4E2E8),
    surfaceVariant = Color(0xFF514048),
    onSurfaceVariant = Color(0xFFDFC1CB),
    outline = Color(0xFFAB8C97),
    outlineVariant = Color(0xFF61434F)
)

@Composable
fun HeartCareTheme(
    darkTheme: Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) HeartCareDarkColors else HeartCareLightColors,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}
