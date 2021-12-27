package com.bselzer.gw2.manager.android.wvw

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import com.bselzer.gw2.manager.android.common.NavigatePage
import com.bselzer.gw2.manager.common.state.core.DialogType
import com.bselzer.gw2.manager.common.state.core.Gw2State
import com.bselzer.gw2.v2.model.extension.world.WorldId
import com.bselzer.ktx.compose.ui.appbar.DropdownMenuIcon
import com.bselzer.ktx.compose.ui.appbar.RefreshIcon

abstract class WvwPage<State>(
    navigationIcon: @Composable () -> Unit,
    protected val state: State,
) : NavigatePage(navigationIcon) {
    @Composable
    override fun Gw2State.appBarActions(): @Composable RowScope.() -> Unit = {
        RefreshIcon {
            val id = WorldId(wvwPref.selectedWorld.get())
            refreshWvwData(id)
            refresh()
        }
        IconButton(onClick = { changeDialog(DialogType.WORLD_SELECTION) }) {
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