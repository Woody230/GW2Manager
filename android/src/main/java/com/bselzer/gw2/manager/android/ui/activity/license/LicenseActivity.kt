package com.bselzer.gw2.manager.android.ui.activity.license

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.ui.activity.common.BaseActivity
import com.bselzer.gw2.manager.android.ui.activity.main.MainActivity
import com.bselzer.library.kotlin.extension.compose.ui.appbar.UpNavigationIcon
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer

class LicenseActivity : BaseActivity() {
    @Composable
    override fun Content() = RelativeBackgroundContent(
        modifier = Modifier.fillMaxSize(),
        title = stringResource(id = R.string.activity_license),
        navigationIcon = { UpNavigationIcon(destination = MainActivity::class.java) },
    ) {
        // TODO license bubble hard to see
        // TODO don't show empty dialog because license doesn't exist
        LibrariesContainer(modifier = Modifier.fillMaxSize())
    }
}