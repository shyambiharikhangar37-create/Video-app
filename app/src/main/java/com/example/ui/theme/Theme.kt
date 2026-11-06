package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldLight,
    onPrimary = TextPrimary,
    primaryContainer = EmeraldDark,
    onPrimaryContainer = TextPrimary,
    secondary = SaffronGold,
    onSecondary = ForestBlack,
    secondaryContainer = SaffronLight,
    onSecondaryContainer = ForestBlack,
    tertiary = CrimsonAccent,
    onTertiary = TextPrimary,
    background = ForestBlack,
    onBackground = TextPrimary,
    surface = ForestSurface,
    onSurface = TextPrimary,
    surfaceVariant = ForestCard,
    onSurfaceVariant = TextSecondary,
    outline = ForestCardBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = ForestBlack.toArgb()
            window.navigationBarColor = ForestBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
