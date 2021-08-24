package com.bselzer.gw2.manager.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.datastore.preferences.core.edit
import com.bselzer.gw2.manager.R
import com.bselzer.gw2.manager.companion.AppCompanion
import com.bselzer.gw2.manager.companion.PreferenceCompanion.BUILD_NUMBER
import com.bselzer.gw2.manager.ui.theme.AppTheme
import com.bselzer.library.kotlin.extension.function.core.hasInternet
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
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
            Download(description)
        }

        if (!download)
        {
            Background()
            MainMenu()
        }
        Toolbar()
    }

    @Preview
    @Composable
    private fun MainContent() = Content(false, "")

    @Composable
    private fun Background() = Image(
        painter = painterResource(id = R.drawable.gw2_two_sylvari),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    @Composable
    private fun Toolbar() = TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
    )

    @Composable
    private fun MainMenu() = Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .clickable { startActivity(Intent(this@MainActivity, SettingsActivity::class.java)) },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.gw2_ice),
                contentDescription = null,
                modifier = Modifier.size(150.dp, 75.dp),
                contentScale = ContentScale.FillBounds
            )
            Text(
                text = "Settings",
                color = Color.Black,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.wrapContentSize()
            )
        }
    }

    @Composable
    private fun Download(description: String) = Box(
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
    private fun PreviewDownload() = Download(description = "Pre-Processing")

    /**
     * Load initial data from the API.
     */
    @Composable
    private fun RetrieveData(setDownloading: (Boolean) -> Unit, setDescription: (String) -> Unit) = LaunchedEffect(true) {
        withContext(Dispatchers.IO)
        {
            val exceptionHandler = CoroutineExceptionHandler { _, throwable -> Timber.e(throwable) }
            launch(exceptionHandler) {
                setDescription("Pre-Processing")

                if (!AppCompanion.APPLICATION.hasInternet())
                {
                    return@launch
                }

                val id = AppCompanion.GW2.build.buildId()
                AppCompanion.DEFAULT_PREFERENCES.edit { pref ->
                    pref[BUILD_NUMBER] = id
                }
            }.invokeOnCompletion { setDownloading(false) }
        }
    }
}