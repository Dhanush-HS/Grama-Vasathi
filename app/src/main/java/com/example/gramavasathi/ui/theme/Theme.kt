package com.example.gramavasathi.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Brown900,
    onPrimary = Brown50,
    primaryContainer = Brown200,
    onPrimaryContainer = Brown900,
    secondary = Brown700,
    onSecondary = Brown50,
    secondaryContainer = Brown200,
    tertiary = Green700,
    onTertiary = Green50,
    background = Brown900,
    onBackground = Cream,
    surface = Brown900,
    onSurface = Cream,
    surfaceVariant = Brown700
)

private val LightColorScheme = lightColorScheme(
    primary = Brown900,
    onPrimary = Brown50,
    primaryContainer = Brown200,
    onPrimaryContainer = Brown900,
    secondary = Brown700,
    onSecondary = Brown50,
    secondaryContainer = Brown200,
    tertiary = Green700,
    onTertiary = Green50,
    background = Cream,
    onBackground = Brown900,
    surface = Color.White,
    onSurface = Brown900,
    surfaceVariant = Brown50
)

@Composable
fun GramaVasathiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Turn off dynamic color by default so our earthy theme shows through on Android 12+
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
