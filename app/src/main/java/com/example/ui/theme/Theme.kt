package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ContrastColorScheme = darkColorScheme(
    primary = ContrastPrimary,
    secondary = ContrastSecondary,
    background = ContrastBackground,
    surface = ContrastSurface,
    onPrimary = ContrastBackground,
    onSecondary = ContrastBackground,
    onBackground = ContrastOnSurface,
    onSurface = ContrastOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = WarmTealPrimary,
    secondary = WarmTealSecondary,
    tertiary = WarmTealTertiary,
    background = SoftBackground,
    surface = SoftSurface,
    onPrimary = SoftSurface,
    onSecondary = SoftSurface,
    onBackground = SoftOnSurface,
    onSurface = SoftOnSurface
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    highContrastEnabled: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        highContrastEnabled -> ContrastColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
