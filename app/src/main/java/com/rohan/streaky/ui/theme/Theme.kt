package com.rohan.streaky.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary          = OrangePrimary,
    onPrimary        = Color.White,
    primaryContainer = Color(0xFFFFE0CC),
    onPrimaryContainer = OrangeDeep,
    secondary        = AmberWarm,
    onSecondary      = Color.White,
    background       = BgLight,
    onBackground     = TextPrimLight,
    surface          = SurfaceLight,
    onSurface        = TextPrimLight,
    surfaceVariant   = Color(0xFFF0F0F0),
    onSurfaceVariant = TextSecLight,
    outline          = BorderLight,
    error            = RedDanger,
    onError          = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary          = OrangePrimary,
    onPrimary        = Color.White,
    primaryContainer = Color(0xFF3D1A06),
    onPrimaryContainer = OrangeSoft,
    secondary        = AmberWarm,
    onSecondary      = Color.Black,
    background       = BgDark,
    onBackground     = TextPrimDark,
    surface          = SurfaceDark,
    onSurface        = TextPrimDark,
    surfaceVariant   = CardDark,
    onSurfaceVariant = TextSecDark,
    outline          = BorderDark,
    error            = RedDanger,
    onError          = Color.White
)

@Composable
fun StreakTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = StreakTypography,
        content = content
    )
}
