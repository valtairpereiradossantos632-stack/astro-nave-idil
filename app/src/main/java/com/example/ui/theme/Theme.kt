package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CosmicColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = CosmicElevated,
    onPrimaryContainer = NeonCyan,
    secondary = NeonPurple,
    onSecondary = Color.White,
    secondaryContainer = CosmicCard,
    onSecondaryContainer = NeonPink,
    tertiary = CosmicGold,
    onTertiary = Color.Black,
    background = CosmicBgDark,
    onBackground = TextPrimary,
    surface = CosmicSurface,
    onSurface = TextPrimary,
    surfaceVariant = CosmicCard,
    onSurfaceVariant = TextSecondary,
    outline = CosmicCardBorder,
    error = PlasmaRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CosmicColorScheme,
        typography = Typography,
        content = content
    )
}
