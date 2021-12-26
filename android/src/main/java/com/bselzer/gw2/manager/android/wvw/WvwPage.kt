package com.bselzer.gw2.manager.android.wvw

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import com.bselzer.gw2.manager.android.common.NavigatePage
import com.bselzer.gw2.manager.common.expect.Gw2Aware
import com.bselzer.ktx.compose.ui.appbar.DropdownMenuIcon
import com.bselzer.ktx.compose.ui.appbar.RefreshIcon

abstract class WvwPage<State>(
    aware: Gw2Aware,
    navigationIcon: @Composable () -> Unit,
    protected val state: State,
) : NavigatePage(aware, navigationIcon) {
    @Composable
    override fun appBarActions(): @Composable RowScope.() -> Unit = {
        RefreshIcon {
            appState.refreshWvwData(wvwPref.selectedWorld.get())
            refresh()
        }
        IconButton(onClick = { appState.showWorldDialog.value = true }) {
            Icon(Icons.Filled.List, contentDescription = "World")
        }

        // Display the dropdown menu for additional page specific icons only if they have been set up.
        DropdownMenuIcon(icons = dropdownIcons())
    }

    /**
     * Refreshes page specific data.
     */
    protected open suspend fun refresh() {}

    /**
     * Lays out the icons specific to this page in a dropdown menu.
     */
    @Composable
    protected open fun dropdownIcons(): (@Composable ((Boolean) -> Unit) -> Unit)? = null
}