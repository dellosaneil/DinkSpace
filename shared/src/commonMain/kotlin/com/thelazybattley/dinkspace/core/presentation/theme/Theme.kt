package com.thelazybattley.dinkspace.core.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Green700,
    onPrimary = Color.White,
    primaryContainer = Green50,
    onPrimaryContainer = Green900,
    secondary = Green900,
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Paper,
    onSurfaceVariant = Slate,
    surfaceContainer = Color.White,
    outline = Mist,
    outlineVariant = Mist,
    error = Danger,
)

private val DarkColors = darkColorScheme(
    primary = Green300,
    onPrimary = Green900,
    primaryContainer = Green900,
    onPrimaryContainer = Green50,
    secondary = Green50,
    onSecondary = Green900,
    background = NightBackground,
    onBackground = NightOnSurface,
    surface = NightBackground,
    onSurface = NightOnSurface,
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = NightOnSurfaceVariant,
    surfaceContainer = NightSurface,
    outline = NightOutline,
    outlineVariant = NightOutline,
)

@Composable
fun DinkSpaceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = DinkSpaceTypography,
        content = content,
    )
}
