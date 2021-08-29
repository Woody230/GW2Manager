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
import com.bselzer.gw2.manager.companion.AppCompanion
import com.bselzer.gw2.manager.companion.PreferenceCompanion.BUILD_NUMBER
import com.bselzer.gw2.manager.ui.theme.AppTheme
import com.bselzer.library.kotlin.extension.function.core.hasInternet
import com.bselzer.library.kotlin.extension.preference.update
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
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MenuButton(name = stringResource(id = R.string.activity_wvw)) {
            startActivity(Intent(this@MainActivity, WvwActivity::class.java))
        }
        Spacer(Modifier.size(20.dp))
        MenuButton(name = stringResource(id = R.string.activity_settings)) {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }
    }

    @Composable
    private fun MenuButton(name: String, onClick: () -> Unit) = Box(
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
                AppCompanion.DATASTORE.update(BUILD_NUMBER, id, this)
            }.invokeOnCompletion { setDownloading(false) }
        }
    }
}