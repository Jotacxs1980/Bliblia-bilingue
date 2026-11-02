package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.data.model.AppThemeMode

data class BibleCustomColors(
    val highlightColor: Color,
    val cardBorder: Color,
    val argumentBackground: Color,
    val argumentText: Color
)

val LocalBibleCustomColors = staticCompositionLocalOf {
    BibleCustomColors(
        highlightColor = SepiaHighlight,
        cardBorder = SepiaBorder,
        argumentBackground = SepiaSurfaceVariant,
        argumentText = SepiaTextSecondary
    )
}

private val SepiaColorScheme = lightColorScheme(
    primary = SepiaPrimary,
    onPrimary = SepiaOnPrimary,
    primaryContainer = SepiaSurfaceVariant,
    onPrimaryContainer = SepiaTextPrimary,
    secondary = GoldAccent,
    background = SepiaBackground,
    onBackground = SepiaTextPrimary,
    surface = SepiaSurface,
    onSurface = SepiaTextPrimary,
    surfaceVariant = SepiaSurfaceVariant,
    onSurfaceVariant = SepiaTextSecondary
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = DarkTextPrimary,
    secondary = GoldAccent,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = LightTextPrimary,
    secondary = GoldAccent,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary
)

@Composable
fun BibliaTheme(
    themeMode: AppThemeMode = AppThemeMode.SEPIA,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.SEPIA -> SepiaColorScheme
        AppThemeMode.DARK -> DarkColorScheme
        AppThemeMode.LIGHT -> LightColorScheme
    }

    val customColors = when (themeMode) {
        AppThemeMode.SEPIA -> BibleCustomColors(
            highlightColor = SepiaHighlight,
            cardBorder = SepiaBorder,
            argumentBackground = SepiaSurfaceVariant,
            argumentText = SepiaTextSecondary
        )
        AppThemeMode.DARK -> BibleCustomColors(
            highlightColor = DarkHighlight,
            cardBorder = DarkBorder,
            argumentBackground = DarkSurfaceVariant,
            argumentText = DarkTextSecondary
        )
        AppThemeMode.LIGHT -> BibleCustomColors(
            highlightColor = LightHighlight,
            cardBorder = LightBorder,
            argumentBackground = LightSurfaceVariant,
            argumentText = LightTextSecondary
        )
    }

    CompositionLocalProvider(LocalBibleCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
