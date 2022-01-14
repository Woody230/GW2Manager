package com.bselzer.gw2.manager.android.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.bselzer.gw2.manager.common.expect.gw2Aware
import com.bselzer.gw2.manager.common.state.core.AppState
import com.bselzer.gw2.manager.common.ui.composable.LocalState
import com.bselzer.ktx.compose.image.ui.LocalImageDispatcher
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI

class MainActivity : AppCompatActivity(), DIAware {
    override val di: DI by closestDI()
    // TODO widgets https://android-developers.googleblog.com/2021/12/announcing-jetpack-glance-alpha-for-app.html

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val aware = di.gw2Aware()
        setContent {
            // TODO better save handling, including of page specific components like the selected objective
            // Remember the last page before activity recreation so that the user doesn't have to manually go back themselves.
            val state = rememberSaveable(saver = listSaver(
                save = { listOf(it.splashRedirectPage.value) },
                restore = {
                    AppState(aware).apply {
                        splashRedirectPage.value = it.getOrNull(0)
                    }
                }
            )) {
                AppState(aware = aware)
            }

            CompositionLocalProvider(
                LocalState provides state,
                LocalImageDispatcher provides Dispatchers.IO
            ) {
                aware.Content {
                    MainPage(closeApplication = { finish() }).Content()
                }
            }
        }
    }
}