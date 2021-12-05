package com.bselzer.gw2.manager.android.ui.activity.setting

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.ui.activity.common.BaseActivity
import com.bselzer.gw2.manager.android.ui.activity.main.MainActivity
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.library.gw2.v2.model.account.token.TokenInfo
import com.bselzer.library.gw2.v2.model.enumeration.extension.account.permissions
import com.bselzer.library.gw2.v2.scope.core.Permission
import com.bselzer.library.kotlin.extension.compose.ui.appbar.UpNavigationIcon
import com.bselzer.library.kotlin.extension.compose.ui.picker.NumberPicker
import com.bselzer.library.kotlin.extension.compose.ui.picker.ValuePicker
import com.bselzer.library.kotlin.extension.compose.ui.preference.PreferenceColumn
import com.bselzer.library.kotlin.extension.compose.ui.preference.PreferenceSection
import com.bselzer.library.kotlin.extension.compose.ui.preference.SwitchPreference
import com.bselzer.library.kotlin.extension.compose.ui.preference.TextFieldDialogPreference
import com.bselzer.library.kotlin.extension.compose.ui.style.hyperlink
import com.bselzer.library.kotlin.extension.compose.ui.style.withColor
import com.bselzer.library.kotlin.extension.coroutine.showToast
import com.bselzer.library.kotlin.extension.function.objects.userFriendly
import com.bselzer.library.kotlin.extension.logging.Logger
import com.bselzer.library.kotlin.extension.settings.compose.nullState
import com.bselzer.library.kotlin.extension.settings.compose.safeState
import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

class SettingsActivity : BaseActivity() {
    // TODO DB clearing

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
     * Displays the preference for selecting the theme.
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
     * Displays the preference for setting the token/api key.
     */
    @Composable
    private fun TokenPreference() {
        var token by commonPref.token.nullState()
        val value = token
        val context = LocalContext.current
        val linkTag = "applications"
        TextFieldDialogPreference(
            iconPainter = painterResource(id = R.drawable.gw2_black_lion_key),
            title = "Token",
            subtitle = if (value.isNullOrBlank()) "Not set" else value,
            dialogSubtitle = buildAnnotatedString {
                withColor(text = "Your account ", color = MaterialTheme.colors.onPrimary)

                // Append a link to where the user can go to look up their api key.
                hyperlink(text = "api key or token.", tag = linkTag, hyperlink = "https://account.arena.net/applications")
            },
            dialogSubtitleOnClick = { offset, text ->
                text.getStringAnnotations(tag = linkTag, start = offset, end = offset).firstOrNull()?.let {
                    // Open the link in the user's browser.
                    val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://account.arena.net/applications"))
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

    /**
     * Displays the refresh interval for WvW data retrieval.
     */
    @OptIn(ExperimentalTime::class)
    @Composable
    private fun RefreshIntervalPreference() {
        var refreshInterval by wvwPref.refreshInterval.safeState()

        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (icon, column) = createRefs()
            Image(
                painter = painterResource(id = R.drawable.gw2_concentration),
                contentDescription = "Refresh Interval",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(48.dp)
                    .constrainAs(icon) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )

            var showDialog by remember { mutableStateOf(false) }
            Column(modifier = Modifier
                .clickable { showDialog = true }
                .constrainAs(column) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(icon.end, 25.dp)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                })
            {
                Text(text = "Refresh Interval", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = refreshInterval.toString(), fontSize = 14.sp)
            }

            if (showDialog) {
                ShowRefreshIntervalDialog({ showDialog = it }, { refreshInterval = it })
            }
        }
    }

    /**
     * Displays the dialog for choosing the refresh interval.
     */
    @OptIn(ExperimentalTime::class)
    @Composable
    private fun ShowRefreshIntervalDialog(setShowDialog: (Boolean) -> Unit, setRefreshInterval: (Duration) -> Unit) {
        // TODO duration unit getting aliased to TimeUnit and preventing use of toInt and toDuration
        val defaultUnit = DurationUnit.MINUTES
        val number = remember { mutableStateOf(wvwPref.refreshInterval.defaultValue.toInt(defaultUnit)) }
        val component = remember { mutableStateOf(defaultUnit) }
        val duration = number.value.toDuration(component.value)

        // Adjust the current value as the component changes to make sure it meets the minimum.
        val min = max(1, Duration.seconds(30).toInt(component.value))
        number.value = max(min, number.value)
        AlertDialog(
            onDismissRequest = { setShowDialog(false) },
            title = {
                Text("Refresh Interval")
            },
            confirmButton = {
                Button(
                    onClick = {
                        setRefreshInterval(duration)
                        setShowDialog(false)
                    },
                ) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                Button(
                    onClick = { setShowDialog(false) },
                ) {
                    Text(text = stringResource(id = R.string.dismiss))
                }
            },
            text = {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NumberPicker(state = number) {
                        number.value = max(min, it)
                    }
                    Spacer(modifier = Modifier.width(5.dp))

                    val components = listOf(DurationUnit.SECONDS, DurationUnit.MINUTES, DurationUnit.HOURS, DurationUnit.DAYS)
                    ValuePicker(state = component, values = components, labels = components.map { component -> component.userFriendly() })
                }
            }
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