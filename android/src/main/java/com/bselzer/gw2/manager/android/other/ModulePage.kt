package com.bselzer.gw2.manager.android.other

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.common.NavigatePage
import com.bselzer.gw2.manager.common.expect.Gw2Aware

class ModulePage(
    aware: Gw2Aware,
    navigationIcon: @Composable () -> Unit
) : NavigatePage(aware, navigationIcon, contentAlignment = Alignment.Center) {
    @Composable
    override fun background() = BackgroundType.ABSOLUTE

    @Composable
    override fun CoreContent() {
        // TODO modules: which worlds are assigned to each side for currently selected world, world selection, overview of WvW data, etc
    }

    @Composable
    override fun title(): String = stringResource(id = R.string.app_name)
}