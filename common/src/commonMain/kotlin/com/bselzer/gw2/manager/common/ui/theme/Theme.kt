package com.bselzer.gw2.manager.common.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.expect.App
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val DarkColorPalette = darkColors(
    primary = Purple700A,
    primaryVariant = Purple700,
    secondary = Teal200,
    secondaryVariant = Teal200,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.White
)

val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200,
    secondaryVariant = Teal500,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.Black
)

@Serializable
enum class Theme {
    @SerialName("Light")
    LIGHT,

    @SerialName("Dark")
    DARK
}

@Composable
internal fun App.AppTheme(content: @Composable () -> Unit) {
    val colors = if (theme() == Theme.DARK) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
    ) {
        CompositionLocalProvider(
            // Need to override so that dark theme uses white instead of default black.
            androidx.compose.material.LocalContentColor provides MaterialTheme.colors.onPrimary
        ) {
            content()
        }
    }
}