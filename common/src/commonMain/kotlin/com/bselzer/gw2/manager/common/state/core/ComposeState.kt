package com.bselzer.gw2.manager.common.state.core

import androidx.compose.runtime.State
import com.bselzer.gw2.manager.common.ui.theme.Theme

/**
 * The state of the UI.
 */
interface ComposeState {
    /**
     * The current page to lay out.
     */
    val currentPage: State<PageType>

    /**
     * The current dialog to lay out.
     */
    val currentDialog: State<DialogType?>

    /**
     * Changes the [currentPage] to the new [page].
     */
    fun changePage(page: PageType)

    /**
     * Changes the [currentDialog] to the new [dialog].
     */
    fun changeDialog(dialog: DialogType)

    /**
     * Resets the [currentDialog] to null.
     */
    fun clearDialog()

    /**
     * Changes the current them to the new [theme].
     */
    suspend fun changeTheme(theme: Theme)
}