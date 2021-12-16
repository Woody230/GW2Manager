package com.bselzer.gw2.manager.android.ui.activity.main

import android.content.Intent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.ui.activity.cache.CacheActivity
import com.bselzer.gw2.manager.android.ui.activity.common.BaseActivity
import com.bselzer.gw2.manager.android.ui.activity.license.LicenseActivity
import com.bselzer.gw2.manager.android.ui.activity.setting.SettingsActivity
import com.bselzer.gw2.manager.android.ui.activity.wvw.WvwActivity
import com.bselzer.gw2.manager.common.expect.App
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.ktx.function.core.hasInternet
import kotlinx.coroutines.*
import org.kodein.di.instance

class MainActivity : BaseActivity() {
    // TODO single activity with decompose navigation
    // TODO widgets https://android-developers.googleblog.com/2021/12/announcing-jetpack-glance-alpha-for-app.html
    private val initializedData by instance<MutableState<Boolean>>(tag = App.INITIAL_DATA_POPULATION)

    @Composable
    override fun Content() = AbsoluteBackgroundContent(
        title = stringResource(id = R.string.app_name)
    ) {
        val (download, setDownloading) = remember { mutableStateOf(!initializedData.value) }
        val (description, setDescription) = remember { mutableStateOf("") }
        Content(download, description)
        RetrieveData(download, setDownloading, setDescription)
    }

    @Composable
    private fun Content(download: Boolean, description: String) = Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (download) {
            ShowDownloading(description)
        } else {
            ShowMainMenu()
        }
    }

    @Preview
    @Composable
    private fun PreviewMenu() = Content(false, "")

    /**
     * Displays all of the buttons within the main menu.
     */
    @Composable
    private fun ShowMainMenu() = ShowMenu(
        // TODO account page

        stringResource(id = R.string.activity_wvw) to {
            // Disable the animation to give the illusion that we haven't swapped screens.
            val intent = Intent(this@MainActivity, WvwActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
        },

        // TODO move to navigation drawer
        stringResource(id = R.string.activity_settings) to { startActivity(Intent(this@MainActivity, SettingsActivity::class.java)) },
        stringResource(id = R.string.activity_cache) to { startActivity(Intent(this@MainActivity, CacheActivity::class.java)) },
        stringResource(id = R.string.activity_license) to { startActivity(Intent(this@MainActivity, LicenseActivity::class.java)) }
    )

    /**
     * Displays the download indicator.
     */
    @Composable
    private fun ShowDownloading(description: String) = RelativeBackgroundColumn(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Text(
            text = description,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        ShowProgressIndicator()
    }

    @Preview
    @Composable
    private fun PreviewDownload() = ShowDownloading(description = "Pre-Processing")

    /**
     * Load initial data from the API.
     */
    @Composable
    private fun RetrieveData(download: Boolean, setDownloading: (Boolean) -> Unit, setDescription: (String) -> Unit) {
        val initialTheme = if (isSystemInDarkTheme()) Theme.DARK else Theme.LIGHT
        LaunchedEffect(download) {
            fun finishedDownloading() {
                setDownloading(false)
                initializedData.value = true
            }

            commonPref.theme.initialize(initialTheme)

            if (!download || !application.hasInternet()) {
                finishedDownloading()
                return@LaunchedEffect
            }

            setDescription("Build Number")
            val newId = gw2Client.build.buildId()
            val buildNumber = commonPref.buildNumber
            if (newId > buildNumber.get()) {
                buildNumber.set(newId)
            }

            finishedDownloading()
        }
    }
}