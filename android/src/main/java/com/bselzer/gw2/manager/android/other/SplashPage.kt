package com.bselzer.gw2.manager.android.other

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.common.NavigatePage
import com.bselzer.gw2.manager.common.expect.Gw2Aware
import com.bselzer.gw2.manager.common.state.AppState.Companion.PageType
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.ktx.function.core.hasInternet

class SplashPage(
    aware: Gw2Aware,
    navigationIcon: @Composable () -> Unit
) : NavigatePage(aware, navigationIcon, contentAlignment = Alignment.Center) {
    @Composable
    override fun background() = BackgroundType.ABSOLUTE

    /**
     * Lays out the splash screen to allow for preprocessing.
     */
    @Composable
    override fun CoreContent() {
        val (description, setDescription) = remember { mutableStateOf("") }
        Downloading(description)
        RetrieveData(setDescription)
    }

    @Composable
    override fun title(): String = stringResource(id = R.string.app_name)

    /**
     * Lays out the download indicator.
     */
    @Composable
    private fun Downloading(description: String) = RelativeBackgroundColumn(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Text(
            text = description,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        ProgressIndicator()
    }

    /**
     * Load initial data from the API.
     */
    @Composable
    private fun RetrieveData(setDescription: (String) -> Unit) {
        val initialTheme = if (isSystemInDarkTheme()) Theme.DARK else Theme.LIGHT
        val context = LocalContext.current
        var selectedPage by appState.page
        LaunchedEffect(selectedPage) {
            fun finishedDownloading() {
                selectedPage = PageType.MODULE
            }

            commonPref.theme.initialize(initialTheme)

            if (!context.hasInternet()) {
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