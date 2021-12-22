package com.bselzer.gw2.manager.android.ui.activity

import androidx.compose.runtime.Composable
import com.bselzer.gw2.manager.android.ui.activity.common.BaseActivity

class MainActivity : BaseActivity() {
    // TODO widgets https://android-developers.googleblog.com/2021/12/announcing-jetpack-glance-alpha-for-app.html

    @Composable
    override fun Content() = MainPage(aware = this, app.theme(), closeApplication = { finish() }).Content()
}