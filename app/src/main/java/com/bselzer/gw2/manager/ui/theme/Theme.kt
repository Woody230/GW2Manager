package com.bselzer.gw2.manager.ui.theme

import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.companion.preference.PreferenceCompanion
import com.bselzer.gw2.manager.ui.kodein.DIAwareActivity
import com.bselzer.library.kotlin.extension.compose.preference.safeRemember
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.White
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.Black
)

@Serializable
enum class Theme {
    @SerialName("Light")
    LIGHT,

    @SerialName("Dark")
    DARK;
}

/**
 * @return the current app theme type, defaulting to [Theme.DARK]
 */
@Composable
fun DIAwareActivity.appThemeType(): Theme {
    val theme by datastore.safeRemember(key = PreferenceCompanion.THEME, Json.encodeToString(Theme.DARK))
    return try {
        Json.decodeFromString(theme)
    } catch (exception: Exception) {
        Timber.e(exception)
        Theme.DARK
    }
}

@Composable
fun DIAwareActivity.AppTheme(content: @Composable () -> Unit) {
    val colors = if (appThemeType() == Theme.DARK) {
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
            LocalContentColor provides MaterialTheme.colors.onPrimary
        ) {
            content()
        }
    }
}