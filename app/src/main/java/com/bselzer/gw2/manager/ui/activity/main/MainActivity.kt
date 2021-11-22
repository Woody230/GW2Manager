package com.bselzer.gw2.manager.ui.activity.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bselzer.gw2.manager.R
import com.bselzer.gw2.manager.companion.preference.PreferenceCompanion.BUILD_NUMBER
import com.bselzer.gw2.manager.ui.activity.common.BaseActivity
import com.bselzer.gw2.manager.ui.activity.setting.SettingsActivity
import com.bselzer.gw2.manager.ui.activity.wvw.WvwActivity
import com.bselzer.gw2.manager.ui.theme.AppTheme
import com.bselzer.library.kotlin.extension.function.core.hasInternet
import com.bselzer.library.kotlin.extension.preference.safeLatest
import com.bselzer.library.kotlin.extension.preference.update
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val (download, setDownloading) = remember { mutableStateOf(true) }
            val (description, setDescription) = remember { mutableStateOf("") }
            Content(download, description)
            RetrieveData(setDownloading, setDescription)
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    private fun Content(download: Boolean, description: String) = AppTheme {
        // TODO transition time?
        AnimatedVisibility(visible = download) {
            ShowDownloading(description)
        }

        if (!download) {
            ShowBackground(R.drawable.gw2_two_sylvari)
            ShowMainMenu()
        }
        ShowAppBar()
    }

    @Preview
    @Composable
    private fun PreviewMenu() = Content(false, "")

    /**
     * Displays the top app bar.
     */
    @Composable
    private fun ShowAppBar() = TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
    )

    /**
     * Displays all of the buttons within the main menu.
     */
    @Composable
    private fun ShowMainMenu() = Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TODO account activity => WvW rank/ability/currency info
        ShowMenuButton(name = stringResource(id = R.string.activity_wvw)) {
            startActivity(Intent(this@MainActivity, WvwActivity::class.java))
        }
        Spacer(Modifier.size(20.dp))
        ShowMenuButton(name = stringResource(id = R.string.activity_settings)) {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }
    }

    /**
     * Displays a single main menu button.
     */
    @Composable
    private fun ShowMenuButton(name: String, onClick: () -> Unit) = Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .wrapContentSize()
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = R.drawable.gw2_ice),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp),
            contentScale = ContentScale.FillBounds
        )
        Text(
            text = name,
            color = Color.Black,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.wrapContentSize()
        )
    }

    /**
     * Displays the download splash screen.
     */
    @Composable
    private fun ShowDownloading(description: String) = Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.gw2_two_asura),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = description,
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.wrapContentSize()
            )
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(0.15f),
                color = Color.Yellow
            )
        }
    }

    @Preview
    @Composable
    private fun PreviewDownload() = ShowDownloading(description = "Pre-Processing")

    /**
     * Load initial data from the API.
     */
    @Composable
    private fun RetrieveData(setDownloading: (Boolean) -> Unit, setDescription: (String) -> Unit) = LaunchedEffect(true) {
        withContext(Dispatchers.IO)
        {
            val exceptionHandler = CoroutineExceptionHandler { _, throwable -> Timber.e(throwable) }
            launch(exceptionHandler) {
                if (!application.hasInternet()) {
                    return@launch
                }

                setDescription("Build Number")
                val newId = gw2Client.build.buildId()
                val existingId = datastore.safeLatest(BUILD_NUMBER)
                if (newId > existingId) {
                    datastore.update(BUILD_NUMBER, newId, this)
                }
            }.invokeOnCompletion { setDownloading(false) }
        }
    }
}