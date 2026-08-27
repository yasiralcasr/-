package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Gold400,
    onPrimary = Navy900,
    primaryContainer = Navy700,
    onPrimaryContainer = Gold300,
    secondary = Cyan400,
    onSecondary = Navy900,
    secondaryContainer = Navy600,
    onSecondaryContainer = Cyan400,
    tertiary = Gold500,
    onTertiary = Navy900,
    background = Navy900,
    onBackground = Slate100,
    surface = Navy800,
    onSurface = Slate100,
    surfaceVariant = Navy700,
    onSurfaceVariant = Slate200,
    outline = Slate700,
    error = RedDanger,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Navy700,
    onPrimary = Color.White,
    primaryContainer = Navy600,
    onPrimaryContainer = Gold300,
    secondary = Cyan600,
    onSecondary = Color.White,
    secondaryContainer = Slate200,
    onSecondaryContainer = Navy800,
    tertiary = Gold600,
    onTertiary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = Navy900,
    surface = Color.White,
    onSurface = Navy900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate800,
    outline = Slate300,
    error = RedDanger,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek executive dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
