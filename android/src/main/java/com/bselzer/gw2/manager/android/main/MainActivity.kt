package com.bselzer.gw2.manager.android.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import com.bselzer.gw2.manager.common.expect.gw2Aware
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
            aware.Content {
                CompositionLocalProvider(
                    LocalImageDispatcher provides Dispatchers.IO
                ) {
                    MainPage(aware = aware, closeApplication = { finish() }).Content()
                }
            }
        }
    }
}