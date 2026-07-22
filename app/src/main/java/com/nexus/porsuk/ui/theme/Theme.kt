@file:Suppress("DEPRECATION")

package com.nexus.porsuk.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Aqua,
    onPrimary = Surface,
    primaryContainer = AquaLight,
    onPrimaryContainer = AquaDeep,
    secondary = AquaDeep,
    onSecondary = Surface,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceAlt,
    onSurfaceVariant = TextMuted,
    outline = BorderLine,
    error = NegativeRed,
    onError = Surface
)

private val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = AquaDark,
    onPrimary = BackgroundDark,
    primaryContainer = BorderDark,
    onPrimaryContainer = AquaBrightDark,
    secondary = AquaBrightDark,
    onSecondary = BackgroundDark,
    background = BackgroundDark,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = BorderDark,
    error = NegativeDark,
    onError = BackgroundDark
)

@Composable
fun PorsukTheme(
    darkTheme: Boolean = false,
    trueBlack: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme && trueBlack -> DarkColorScheme.copy(
            background = Color.Black,
            surface = Color(0xFF070B0E),
            surfaceVariant = Color(0xFF0B1116),
            outline = Color(0xFF1E293B)
        )
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
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
