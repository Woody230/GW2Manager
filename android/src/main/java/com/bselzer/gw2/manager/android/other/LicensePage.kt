package com.bselzer.gw2.manager.android.other

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.common.BackgroundType
import com.bselzer.gw2.manager.android.common.NavigatePage
import com.bselzer.gw2.manager.common.state.core.Gw2State
import com.bselzer.ktx.library.LibraryColumn
import com.mikepenz.aboutlibraries.entity.Library

/**
 * The page for laying out the libraries used in the app and their associated licenses.
 */
class LicensePage(
    navigationIcon: @Composable () -> Unit,
    private val libraries: List<Library>
) : NavigatePage(navigationIcon) {
    @Composable
    override fun background() = BackgroundType.RELATIVE

    @Composable
    override fun Gw2State.CoreContent() = LibraryColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(all = 8.dp),
        backgroundColor = Color.Transparent, // Use the relative background instead.
        itemElevation = 0.dp, // Disable the shadow.
        libraries = libraries.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { library -> library.name })
    )

    @Composable
    override fun title(): String = stringResource(id = R.string.activity_license)
}