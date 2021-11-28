package com.bselzer.gw2.manager.android.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.ui.activity.common.BaseActivity
import com.bselzer.gw2.manager.android.ui.activity.main.MainActivity
import com.bselzer.gw2.manager.android.ui.theme.AppTheme
import com.bselzer.gw2.manager.android.ui.theme.Theme
import com.bselzer.library.gw2.v2.model.account.token.TokenInfo
import com.bselzer.library.gw2.v2.model.enumeration.extension.account.permissions
import com.bselzer.library.gw2.v2.scope.core.Permission
import com.bselzer.library.kotlin.extension.compose.ui.appbar.MaterialAppBar
import com.bselzer.library.kotlin.extension.compose.ui.appbar.UpNavigationIcon
import com.bselzer.library.kotlin.extension.compose.ui.picker.NumberPicker
import com.bselzer.library.kotlin.extension.compose.ui.picker.ValuePicker
import com.bselzer.library.kotlin.extension.compose.ui.style.hyperlink
import com.bselzer.library.kotlin.extension.coroutine.showToast
import com.bselzer.library.kotlin.extension.function.objects.userFriendly
import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

class SettingsActivity : BaseActivity() {
    // TODO DB clearing
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Content() }
    }

    @Composable
    private fun Content() = AppTheme {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            MaterialAppBar(title = R.string.app_name, navigationIcon = { UpNavigationIcon(destination = MainActivity::class.java) })

            RelativeBackgroundColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                contentModifier = Modifier.padding(25.dp),
                alignment = Alignment.TopStart,
                contentHorizontalAlignment = Alignment.Start,
            ) {
                ShowThemePreference()
                Spacer(modifier = Modifier.height(10.dp))
                Divider(thickness = 5.dp)
                Spacer(modifier = Modifier.height(10.dp))
                ShowTokenPreference()
                Spacer(modifier = Modifier.height(10.dp))
                Divider(thickness = 5.dp)
                Spacer(modifier = Modifier.height(10.dp))

                // TODO icon
                Text(text = stringResource(R.string.activity_wvw), color = MaterialTheme.colors.primary, fontSize = 14.sp, modifier = Modifier.padding(start = 73.dp))
                Spacer(modifier = Modifier.height(10.dp))
                ShowRefreshIntervalPreference()
            }
        }
    }

    /**
     * Displays the preference for selecting the theme.
     */
    @Composable
    private fun ShowThemePreference() {
        var theme by commonPref.rememberTheme()
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (icon, column, switch) = createRefs()
            Image(
                painter = painterResource(id = if (theme == Theme.LIGHT) R.drawable.gw2_eternity else R.drawable.gw2_sunrise),
                contentDescription = "Theme",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(48.dp)
                    .constrainAs(icon) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )

            Column(modifier = Modifier.constrainAs(column) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(icon.end, 25.dp)
                end.linkTo(switch.start, 25.dp)
                width = Dimension.fillToConstraints
            }) {
                Text(text = "Theme", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = theme.userFriendly(), fontSize = 14.sp)
            }

            Switch(
                checked = theme == Theme.DARK,
                onCheckedChange = { isDarkMode -> theme = if (isDarkMode) Theme.DARK else Theme.LIGHT },
                modifier = Modifier.constrainAs(switch) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                })
        }
    }

    /**
     * Displays the preference for setting the token/api key.
     */
    @Composable
    private fun ShowTokenPreference() {
        var token by commonPref.rememberToken()

        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (icon, column) = createRefs()
            Image(
                painter = painterResource(id = R.drawable.gw2_black_lion_key),
                contentDescription = "Token",
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
                }) {
                Text(text = "Token", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                val value = token
                Text(text = if (value.isNullOrBlank()) "Not set" else value, fontSize = 14.sp)
            }

            if (showDialog) {
                ShowTokenDialog({ showDialog = it }, { token = it })
            }
        }
    }

    /**
     * Shows the dialog for setting the token.
     */
    @Composable
    private fun ShowTokenDialog(setShowDialog: (Boolean) -> Unit, setToken: (String?) -> Unit) {
        var editText by remember { mutableStateOf("") }
        val context = LocalContext.current

        // TODO delete button
        AlertDialog(
            onDismissRequest = { setShowDialog(false) },
            title = {
                Text("Token")
            },
            confirmButton = {
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            // Validate the new token before committing it.
                            val token = editText.trim()
                            if (token.isBlank()) {
                                setToken(null)
                                return@launch
                            }

                            try {
                                withContext(Dispatchers.IO) {
                                    val tokenInfo = gw2Client.token.information(token = token)
                                    database.put(tokenInfo)
                                    initializePreferences(token, tokenInfo)
                                    setToken(token)
                                    Timber.d("Set client token to $token")
                                }
                            } catch (ex: ClientRequestException) {
                                showToast(context, "Unable to save the token: ${ex.response.readText()}", Toast.LENGTH_LONG)
                            } catch (ex: Exception) {
                                showToast(context, "Unable to save the token.", Toast.LENGTH_SHORT)
                            }
                            setShowDialog(false)
                        }
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
                val tag = "applications"
                val message = buildAnnotatedString {
                    val style = LocalTextStyle.current.toSpanStyle()
                    withStyle(style = style.copy(color = MaterialTheme.colors.onPrimary)) {
                        append("Your account ")
                    }

                    // Append a link to where the user can go to look up their api key.
                    pushStringAnnotation(tag = tag, annotation = "https://account.arena.net/applications")
                    withStyle(style = style.hyperlink()) {
                        append("api key or token.")
                    }
                    pop()
                }

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ClickableText(text = message) { offset ->
                        message.getStringAnnotations(tag = tag, start = offset, end = offset).firstOrNull()?.let {
                            // Open the link in the user's browser.
                            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://account.arena.net/applications"))
                            startActivity(intent)
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))
                    TextField(value = editText, onValueChange = { editText = it })
                }
            }
        )
    }

    /**
     * Displays the refresh interval for WvW data retrieval.
     */
    @OptIn(ExperimentalTime::class)
    @Composable
    private fun ShowRefreshIntervalPreference() {
        var refreshInterval by wvwPref.rememberRefreshInterval()

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
        val number = remember { mutableStateOf(wvwPref.refreshIntervalDefault.toInt(wvwPref.refreshIntervalDefaultUnit)) }
        val component = remember { mutableStateOf(wvwPref.refreshIntervalDefaultUnit) }
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
            Timber.d("Token permissions: $permissions")

            // TODO scope processor to automatically verify permissions
            if (permissions.contains(Permission.ACCOUNT)) {
                val account = gw2Client.account.account(token)
                wvwPref.initializeSelectedWorld(account.world)
            }
        } catch (ex: Exception) {
            Timber.e("Unable to initialize preferences.", ex)
        }
    }
}