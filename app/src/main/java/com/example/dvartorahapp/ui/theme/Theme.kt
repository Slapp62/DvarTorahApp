package com.example.dvartorahapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary              = Ink900,
    onPrimary            = White,
    primaryContainer     = Sky50,
    onPrimaryContainer   = Ink900,

    secondary            = Gold600,
    onSecondary          = White,
    secondaryContainer   = Gold100,
    onSecondaryContainer = Gold600,

    background           = Sand50,
    onBackground         = Ink950,

    surface              = White,
    onSurface            = Ink900,
    surfaceVariant       = Sand100,
    onSurfaceVariant     = Stone500,

    outline              = Stone300,
    outlineVariant       = Sand100,

    error                = Color(0xFFDC2626),
    onError              = White
)

private val DarkColorScheme = darkColorScheme(
    primary              = Sky100,
    onPrimary            = Ink950,
    primaryContainer     = Ink700,
    onPrimaryContainer   = White,

    secondary            = Gold500,
    onSecondary          = Ink950,
    secondaryContainer   = Gold600.copy(alpha = 0.28f),
    onSecondaryContainer = Gold100,

    background           = Ink950,
    onBackground         = Sand50,

    surface              = Ink900,
    onSurface            = Sand50,
    surfaceVariant       = Ink800,
    onSurfaceVariant     = Sky100.copy(alpha = 0.72f),

    outline              = Ink700,
    outlineVariant       = Ink800,

    error                = Color(0xFFF87171),
    onError              = Ink950
)

@Composable
fun DvarTorahAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography  = Typography,
        content     = content
    )
}
