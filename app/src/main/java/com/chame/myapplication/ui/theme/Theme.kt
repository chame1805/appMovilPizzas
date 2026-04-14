package com.chame.myapplication.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PizzaOrangeLight,
    background = BackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    onBackground = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    surfaceVariant = SurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = PizzaOrange,
    background = BackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    onBackground = OnSurfaceLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    surfaceVariant = SurfaceLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
