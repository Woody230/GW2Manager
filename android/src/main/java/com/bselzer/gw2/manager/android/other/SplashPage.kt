package com.bselzer.gw2.manager.android.other

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.common.BackgroundType
import com.bselzer.gw2.manager.android.common.NavigatePage
import com.bselzer.gw2.manager.common.state.core.Gw2State
import com.bselzer.gw2.manager.common.state.core.PageType
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.ktx.logging.Logger

class SplashPage(
    navigationIcon: @Composable () -> Unit
) : NavigatePage(navigationIcon) {
    @Composable
    override fun background() = BackgroundType.ABSOLUTE

    /**
     * Lays out the splash screen to allow for preprocessing.
     */
    @Composable
    override fun Gw2State.CoreContent() {
        val (description, setDescription) = remember { mutableStateOf("") }
        Downloading(description)
        RetrieveData(setDescription)
    }

    @Composable
    override fun title(): String = stringResource(id = R.string.app_name)

    @Composable
    override fun contentAlignment() = Alignment.Center

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
    private fun Gw2State.RetrieveData(setDescription: (String) -> Unit) {
        val initialTheme = if (isSystemInDarkTheme()) Theme.DARK else Theme.LIGHT
        LaunchedEffect(this) {
            try {
                setDescription("Preferences")
                commonPref.theme.initialize(initialTheme)

                setDescription("World vs. World")
                initializeWvwData()

                // Build number has been static for months https://github.com/gw2-api/issues/issues/1 and have to use assetcdn
                setDescription("Build Number")
                var newId = assetCdnClient.latest().id
                if (newId <= 0) {
                    Logger.w("Asset CDN build id is not valid. Defaulting to the api.")
                    newId = gw2Client.build.buildId()
                }

                val buildNumber = commonPref.buildNumber
                val oldId = buildNumber.get()
                Logger.d("Old build id: $oldId | New build id: $newId")
                if (newId > oldId) {
                    buildNumber.set(newId)
                }
            } finally {
                changePage(PageType.MODULE)
            }
        }
    }
}