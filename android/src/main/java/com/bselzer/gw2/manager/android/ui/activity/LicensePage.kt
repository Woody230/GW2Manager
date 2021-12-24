package com.bselzer.gw2.manager.android.ui.activity

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.ui.activity.common.BasePage
import com.bselzer.gw2.manager.common.expect.Gw2Aware
import com.bselzer.ktx.compose.ui.appbar.UpNavigationIcon
import com.bselzer.ktx.library.LibraryColumn
import com.mikepenz.aboutlibraries.entity.Library

/**
 * The page for laying out the libraries used in the app and their associated licenses.
 */
class LicensePage(
    aware: Gw2Aware,
    private val navigateUp: () -> Unit,
    private val libraries: List<Library>
) : BasePage(aware) {
    @Composable
    override fun Content() = RelativeBackgroundContent(
        modifier = Modifier.fillMaxSize(),
        title = stringResource(id = R.string.activity_license),
        navigationIcon = { UpNavigationIcon(onClick = navigateUp) },
    ) {
        LibraryColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(all = 8.dp),
            backgroundColor = Color.Transparent, // Use the relative background instead.
            itemElevation = 0.dp, // Disable the shadow.
            libraries = libraries.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { library -> library.name })
        )
    }
}