package com.bselzer.gw2.manager.android.ui.activity.license

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.ui.activity.common.BaseActivity
import com.bselzer.gw2.manager.android.ui.activity.main.MainActivity
import com.bselzer.ktx.compose.ui.appbar.UpNavigationIcon
import com.bselzer.ktx.library.LibraryColumn
import com.bselzer.ktx.library.libraries

class LicenseActivity : BaseActivity() {
    @Composable
    override fun Content() = RelativeBackgroundContent(
        modifier = Modifier.fillMaxSize(),
        title = stringResource(id = R.string.activity_license),
        navigationIcon = { UpNavigationIcon(destination = MainActivity::class.java) },
    ) {
        LibraryColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(all = 8.dp),
            backgroundColor = Color.Transparent, // Use the relative background instead.
            itemElevation = 0.dp, // Disable the shadow.
            libraries = libraries().sortedBy { library -> library.name }
        )
    }
}