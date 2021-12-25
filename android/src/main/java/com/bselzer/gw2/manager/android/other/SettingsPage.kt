package com.bselzer.gw2.manager.android.other

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.common.NavigatePage
import com.bselzer.gw2.manager.common.expect.Gw2Aware
import com.bselzer.gw2.manager.common.expect.LocalTheme
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.gw2.v2.model.account.token.TokenInfo
import com.bselzer.gw2.v2.model.enumeration.extension.account.permissions
import com.bselzer.gw2.v2.scope.core.Permission
import com.bselzer.ktx.compose.ui.preference.*
import com.bselzer.ktx.compose.ui.style.hyperlink
import com.bselzer.ktx.compose.ui.style.withColor
import com.bselzer.ktx.coroutine.showToast
import com.bselzer.ktx.function.objects.userFriendly
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.settings.compose.nullState
import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

/**
 * The page for the user to select and manage preferences.
 */
class SettingsPage(
    aware: Gw2Aware,
    navigationIcon: @Composable () -> Unit
) : NavigatePage(aware, navigationIcon) {
    @Composable
    override fun background() = BackgroundType.RELATIVE

    @Composable
    override fun CoreContent() = PreferenceColumn(
        modifier = Modifier.padding(25.dp),
        contents = arrayOf(
            { ThemePreference() },
            { TokenPreference() },
            {
                PreferenceSection(
                    iconPainter = painterResource(R.drawable.gw2_rank_dolyak),
                    title = stringResource(R.string.activity_wvw)
                ) {
                    RefreshIntervalPreference()
                }
            }
        )
    )

    @Composable
    override fun title(): String = stringResource(id = R.string.activity_settings)

    override fun Modifier.background(): Modifier = composed { verticalScroll(rememberScrollState()) }

    /**
     * Lays out the preference for selecting the theme.
     */
    @Composable
    private fun ThemePreference() {
        val theme = LocalTheme.current
        SwitchPreference(
            iconPainter = painterResource(if (theme == Theme.LIGHT) R.drawable.gw2_sunrise else R.drawable.gw2_twilight),
            title = "Theme",
            subtitle = theme.userFriendly(),
            checked = theme != Theme.LIGHT,
            onStateChanged = {
                CoroutineScope(Dispatchers.IO).launch {
                    commonPref.theme.set(if (it) Theme.DARK else Theme.LIGHT)
                }
            }
        )
    }

    /**
     * Lays out the preference for setting the token/api key.
     */
    @Composable
    private fun TokenPreference() {
        var token by commonPref.token.nullState()
        val value = token
        val context = LocalContext.current
        val linkTag = "applications"
        val hyperlink = "https://account.arena.net/applications"
        TextFieldDialogPreference(
            iconPainter = painterResource(id = R.drawable.gw2_black_lion_key),
            title = "Token",
            subtitle = if (value.isNullOrBlank()) "Not set" else value,
            dialogSubtitle = buildAnnotatedString {
                withColor(text = "Your account ", color = MaterialTheme.colors.onPrimary)

                // Append a link to where the user can go to look up their api key.
                hyperlink(text = "api key or token.", tag = linkTag, hyperlink = hyperlink)
            },
            dialogSubtitleOnClick = { offset, text ->
                text.getStringAnnotations(tag = linkTag, start = offset, end = offset).firstOrNull()?.let {
                    // Open the link in the user's browser.
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(hyperlink))
                    startActivity(context, intent, null)
                }
            },
            onStateChanged = { newValue ->
                CoroutineScope(Dispatchers.Main).launch {
                    // Validate the new token before committing it.
                    if (newValue.isNullOrBlank()) {
                        token = null
                        return@launch
                    }

                    try {
                        withContext(Dispatchers.IO) {
                            val tokenInfo = gw2Client.token.information(token = newValue)
                            database.put(tokenInfo)
                            initializePreferences(newValue, tokenInfo)
                            token = newValue
                            Logger.d("Set client token to $token")
                        }
                    } catch (ex: ClientRequestException) {
                        showToast(context, "Unable to save the token: ${ex.response.readText()}", Toast.LENGTH_LONG)
                    } catch (ex: Exception) {
                        showToast(context, "Unable to save the token.", Toast.LENGTH_SHORT)
                    }
                }
            }
        )
    }

    /**
     * Displays the refresh interval for WvW data retrieval.
     */
    @OptIn(ExperimentalTime::class)
    @Composable
    private fun RefreshIntervalPreference() {
        var refreshInterval by wvwPref.refreshInterval.nullState()
        val initial = wvwPref.refreshInterval.defaultValue
        val initialUnit = wvwPref.refreshIntervalDefaultUnit
        val value = refreshInterval ?: initial

        DurationDialogPreference(
            onStateChanged = { refreshInterval = it },
            iconPainter = painterResource(id = R.drawable.gw2_concentration),
            title = "Refresh Interval",
            subtitle = value.toString(),
            initialAmount = initial.toInt(initialUnit),
            initialUnit = initialUnit,
            minimum = 30.seconds,
            units = listOf(DurationUnit.SECONDS, DurationUnit.MINUTES, DurationUnit.HOURS, DurationUnit.DAYS)
        )
    }

    /**
     * Set up other preferences based on the [tokenInfo].
     */
    @OptIn(ExperimentalTime::class)
    private suspend fun initializePreferences(token: String, tokenInfo: TokenInfo) {
        try {
            val permissions = tokenInfo.permissions()
            Logger.d("Token permissions: $permissions")

            // TODO scope processor to automatically verify permissions
            if (permissions.contains(Permission.ACCOUNT)) {
                val account = gw2Client.account.account(token)
                wvwPref.selectedWorld.initialize(account.world)
            }
        } catch (ex: Exception) {
            Logger.e(ex, "Unable to initialize preferences.")
        }
    }
}