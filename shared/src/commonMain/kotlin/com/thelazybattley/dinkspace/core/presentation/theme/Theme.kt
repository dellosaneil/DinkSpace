package com.thelazybattley.dinkspace.core.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Blue700,
    onPrimary = Color.White,
    primaryContainer = Blue50,
    onPrimaryContainer = Blue900,
    secondary = Blue900,
    onSecondary = Color.White,
    secondaryContainer = Blue50,
    onSecondaryContainer = Blue900,
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
    primary = Blue300,
    onPrimary = Blue900,
    primaryContainer = Blue900,
    onPrimaryContainer = Blue50,
    secondary = Blue50,
    onSecondary = Blue900,
    secondaryContainer = Blue900,
    onSecondaryContainer = Blue50,
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
