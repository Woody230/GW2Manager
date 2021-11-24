package com.bselzer.gw2.manager.ui.activity.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.bselzer.gw2.manager.R
import com.bselzer.gw2.manager.companion.preference.PreferenceCompanion.BUILD_NUMBER
import com.bselzer.gw2.manager.ui.activity.common.BaseActivity
import com.bselzer.gw2.manager.ui.activity.setting.SettingsActivity
import com.bselzer.gw2.manager.ui.activity.wvw.WvwActivity
import com.bselzer.gw2.manager.ui.theme.AppTheme
import com.bselzer.library.kotlin.extension.compose.ui.ShowAppBarTitle
import com.bselzer.library.kotlin.extension.compose.ui.ShowBackground
import com.bselzer.library.kotlin.extension.function.core.hasInternet
import com.bselzer.library.kotlin.extension.preference.safeLatest
import com.bselzer.library.kotlin.extension.preference.update
import kotlinx.coroutines.*
import org.kodein.di.instance

class MainActivity : BaseActivity() {
    private val initializedData by instance<MutableState<Boolean>>(tag = "InitialDataPopulation")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val (download, setDownloading) = remember { mutableStateOf(!initializedData.value) }
            val (description, setDescription) = remember { mutableStateOf("") }
            Content(download, description)
            RetrieveData(download, setDownloading, setDescription)
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    private fun Content(download: Boolean, description: String) = AppTheme {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ShowAppBar()

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                ShowBackground(drawableId = R.drawable.gw2_two_sylvari)

                if (download) {
                    ShowDownloading(description)
                } else {
                    ShowMainMenu()
                }
            }
        }
    }

    @Preview
    @Composable
    private fun PreviewMenu() = Content(false, "")

    /**
     * Displays the top app bar.
     */
    @Composable
    private fun ShowAppBar() = TopAppBar(
        title = { ShowAppBarTitle(title = R.string.app_name) },
    )

    /**
     * Displays all of the buttons within the main menu.
     */
    @Composable
    private fun ShowMainMenu() = ShowMenu(
        stringResource(id = R.string.activity_wvw) to {
            // Disable the animation to give the illusion that we haven't swapped screens.
            val intent = Intent(this@MainActivity, WvwActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
        },
        stringResource(id = R.string.activity_settings) to {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }
    )

    /**
     * Displays the download indicator.
     */
    @Composable
    private fun ShowDownloading(description: String) = Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        ShowRelativeBackground()

        Column(
            modifier = Modifier.wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = description,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            ProgressIndicator()
        }
    }

    @Preview
    @Composable
    private fun PreviewDownload() = ShowDownloading(description = "Pre-Processing")

    /**
     * Load initial data from the API.
     */
    @Composable
    private fun RetrieveData(download: Boolean, setDownloading: (Boolean) -> Unit, setDescription: (String) -> Unit) = LaunchedEffect(download) {
        fun finishedDownloading() {
            setDownloading(false)
            initializedData.value = true
        }

        if (!download || !application.hasInternet()) {
            finishedDownloading()
            return@LaunchedEffect
        }

        setDescription("Build Number")
        val newId = gw2Client.build.buildId()
        val existingId = datastore.safeLatest(BUILD_NUMBER)
        if (newId > existingId) {
            datastore.update(BUILD_NUMBER, newId, this)
        }

        finishedDownloading()
    }
}