package com.bselzer.gw2.manager.android.ui.activity.setting

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.ui.activity.common.BaseActivity
import com.bselzer.gw2.manager.android.ui.activity.main.MainActivity
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.gw2.v2.model.account.token.TokenInfo
import com.bselzer.gw2.v2.model.enumeration.extension.account.permissions
import com.bselzer.gw2.v2.scope.core.Permission
import com.bselzer.ktx.compose.ui.appbar.UpNavigationIcon
import com.bselzer.ktx.compose.ui.picker.NumberPicker
import com.bselzer.ktx.compose.ui.picker.ValuePicker
import com.bselzer.ktx.compose.ui.preference.*
import com.bselzer.ktx.compose.ui.style.hyperlink
import com.bselzer.ktx.compose.ui.style.withColor
import com.bselzer.ktx.coroutine.showToast
import com.bselzer.ktx.function.objects.userFriendly
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.settings.compose.nullState
import com.bselzer.ktx.settings.compose.safeState
import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import kotlin.math.max
import kotlin.math.min
import kotlin.time.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

class SettingsActivity : BaseActivity() {

    @Composable
    override fun Content() = RelativeBackgroundContent(
        backgroundModifier = Modifier.verticalScroll(rememberScrollState()),
        title = stringResource(R.string.activity_settings),
        navigationIcon = { UpNavigationIcon(destination = MainActivity::class.java) },
    ) {
        PreferenceColumn(
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
    }

    /**
     * Lays out the preference for selecting the theme.
     */
    @Composable
    private fun ThemePreference() {
        var theme by commonPref.theme.safeState()
        SwitchPreference(
            iconPainter = painterResource(if (theme == Theme.LIGHT) R.drawable.gw2_sunrise else R.drawable.gw2_twilight),
            title = "Theme",
            subtitle = theme.userFriendly(),
            checked = theme != Theme.LIGHT,
            onStateChanged = { theme = if (it) Theme.DARK else Theme.LIGHT }
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
                    val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(hyperlink))
                    startActivity(intent)
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

    /*
        // TODO DurationUnit <=> TimeUnit alias -- 1.6.0 fixes

    /**
     * Displays the refresh interval for WvW data retrieval.
     */
    @OptIn(ExperimentalTime::class)
    @Composable
    private fun RefreshIntervalPreference() {
        var refreshInterval by wvwPref.refreshInterval.nullState()
        val value = refreshInterval ?: wvwPref.refreshInterval.defaultValue
        val unit = wvwPref.refreshIntervalDefaultUnit

        DurationDialogPreference(
            onStateChanged = { refreshInterval.value = it },
            iconPainter = painterResource(id = R.drawable.gw2_concentration),
            title = "Refresh Interval",
            subtitle = value.toString(),
            initialAmount = value.toInt(DurationUnit.MINUTES),
            initialUnit = DurationUnit.MINUTES,
            minimum = 30.seconds,
            units = listOf(DurationUnit.SECONDS, DurationUnit.MINUTES, DurationUnit.HOURS, DurationUnit.DAYS)
        )
    }
    */

    /**
     * Lays out the refresh interval for WvW data retrieval.
     */
    @OptIn(ExperimentalTime::class)
    @Composable
    private fun RefreshIntervalPreference() {
        var refreshInterval by wvwPref.refreshInterval.nullState()
        val value = refreshInterval ?: wvwPref.refreshInterval.defaultValue
        val unit = remember { mutableStateOf(DurationUnit.MINUTES) }

        // Adjust the current value as the component changes to make sure it is bounded.
        val minimum = 30.seconds
        val maximum = Int.MAX_VALUE.days
        val convertedMin = max(1, minimum.toInt(unit.value))
        val convertedMax = maximum.toInt(unit.value)
        fun bounded(value: Int) = min(convertedMax, max(convertedMin, value))
        val amount = remember { mutableStateOf(value.toInt(DurationUnit.MINUTES)) }
        amount.value = bounded(amount.value)

        val temp = remember { mutableStateOf(refreshInterval) }
        temp.value = amount.value.toDuration(unit.value)
        DialogPreference(
            state = temp,
            onStateChanged = { refreshInterval = it },
            iconPainter = painterResource(id = R.drawable.gw2_concentration),
            title = "Refresh Interval",
            subtitle = value.toString(),
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Converted minimum can produce 0 because of being rounded down so set the minimum bound to at least 1. (ex: 30 seconds => 0 minutes)
                NumberPicker(value = amount.value, range = convertedMin..convertedMax) {
                    amount.value = bounded(it)
                }
                Spacer(modifier = Modifier.width(25.dp))

                val units = listOf(DurationUnit.SECONDS, DurationUnit.MINUTES, DurationUnit.HOURS, DurationUnit.DAYS)
                ValuePicker(value = unit.value, values = units, labels = units.map { component -> component.userFriendly() }) {
                    unit.value = it
                }
            }
        }
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