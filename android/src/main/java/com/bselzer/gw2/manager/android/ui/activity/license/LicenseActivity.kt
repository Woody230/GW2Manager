package com.bselzer.gw2.manager.android.ui.activity.license

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.android.ui.activity.common.BaseActivity
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer

class LicenseActivity : BaseActivity() {
    @Composable
    override fun Content() = RelativeBackgroundColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        LibrariesContainer(modifier = Modifier.fillMaxSize())
    }
}