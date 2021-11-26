package com.bselzer.gw2.manager.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bselzer.gw2.manager.R
import com.bselzer.gw2.manager.ui.activity.common.BaseActivity
import com.bselzer.gw2.manager.ui.activity.main.MainActivity
import com.bselzer.gw2.manager.ui.theme.AppTheme
import com.bselzer.gw2.manager.ui.theme.Theme
import com.bselzer.library.gw2.v2.model.account.token.TokenInfo
import com.bselzer.library.gw2.v2.model.enumeration.extension.account.permissions
import com.bselzer.library.gw2.v2.scope.core.Permission
import com.bselzer.library.kotlin.extension.compose.ui.ShowAppBarTitle
import com.bselzer.library.kotlin.extension.coroutine.showToast
import com.bselzer.library.kotlin.extension.function.objects.userFriendly
import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.time.ExperimentalTime

class SettingsActivity : BaseActivity() {
    // TODO DB clearing
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Content() }
    }

    @Composable
    private fun Content() = AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopAppBar(
                title = { ShowAppBarTitle(title = R.string.app_name) },
                navigationIcon = { ShowUpNavigationIcon(intent = Intent(this@SettingsActivity, MainActivity::class.java)) }
            )

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                ShowRelativeBackground()

                Column(
                    modifier = Modifier.padding(25.dp)
                ) {
                    ShowThemePreference()
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(thickness = 5.dp)
                    Spacer(modifier = Modifier.height(10.dp))
                    ShowTokenPreference()

                    // TODO refresh interval
                }
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

            Switch(checked = theme == Theme.DARK, onCheckedChange = { isDarkMode -> theme = if (isDarkMode) Theme.DARK else Theme.LIGHT }, modifier = Modifier.constrainAs(switch) {
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

            val context = LocalContext.current
            Column(modifier = Modifier
                .clickable {
                    val editText = EditText(context)
                    AlertDialog
                        .Builder(context)
                        .setTitle("Token")
                        .setMessage("Your account api key or token. https://account.arena.net/applications")
                        .setView(editText)
                        .setPositiveButton(android.R.string.ok) { dialog, which ->
                            CoroutineScope(Dispatchers.Main).launch {
                                // Validate the new token before committing it.
                                val newValue = editText.text
                                    ?.toString()
                                    ?.trim() ?: ""
                                if (newValue.isBlank()) {
                                    token = null
                                    return@launch
                                }

                                try {
                                    withContext(Dispatchers.IO) {
                                        val tokenInfo = gw2Client.token.information(token = newValue)
                                        database.put(tokenInfo)
                                        initializePreferences(newValue, tokenInfo)
                                        token = newValue
                                        Timber.d("Set client token to $newValue")
                                    }
                                } catch (ex: ClientRequestException) {
                                    showToast(context, "Unable to save the token: ${ex.response.readText()}", Toast.LENGTH_LONG)
                                } catch (ex: Exception) {
                                    showToast(context, "Unable to save the token.", Toast.LENGTH_SHORT)
                                }
                            }
                            dialog.dismiss()
                        }
                        .setNeutralButton(android.R.string.cancel) { dialog, which -> dialog.cancel() }
                        .setNegativeButton("Delete") { dialog, which ->
                            token = null
                            dialog.dismiss()
                        }
                        .show()
                }
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
        }
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