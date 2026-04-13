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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PizzaPrimaryLight,
    onPrimary = Color.Black,
    primaryContainer = PizzaPrimary,
    secondary = PizzaSecondary,
    onSecondary = Color.Black,
    tertiary = PizzaTertiary,
    onTertiary = Color.Black,
    background = BackgroundDark,
    surface = SurfaceDark,
    onSurface = Color.White,
    error = PizzaError,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PizzaPrimary,
    onPrimary = Color.White,
    primaryContainer = PizzaPrimaryLight,
    secondary = PizzaSecondary,
    onSecondary = Color.Black,
    tertiary = PizzaTertiary,
    onTertiary = Color.Black,
    background = BackgroundLight,
    surface = SurfaceLight,
    onSurface = Color.Black,
    error = PizzaError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,  // Disabled to use our custom pizzeria theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}