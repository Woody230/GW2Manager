package com.bselzer.gw2.manager.android.wvw.page

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.bselzer.gw2.manager.android.common.BasePage
import com.bselzer.gw2.manager.common.expect.Gw2Aware
import com.bselzer.ktx.compose.ui.appbar.MaterialAppBar
import com.bselzer.ktx.compose.ui.appbar.UpNavigationIcon

abstract class WvwContentPage<State>(
    aware: Gw2Aware,
    private val navigateUp: () -> Unit,
    private val appBarActions: @Composable RowScope.() -> Unit,
    protected val state: State
) : BasePage(aware) {
    /**
     * Lays out the top app bar.
     */
    @Composable
    protected fun TopAppBar() {
        val context = LocalContext.current
        MaterialAppBar(
            title = topAppBarTitle(),
            navigationIcon = {
                UpNavigationIcon { navigateUp() }
            },
            actions = {
                appBarActions()
                TopAppBarActions()
            }
        )
    }

    /**
     * The title of the [TopAppBar].
     */
    @Composable
    abstract fun topAppBarTitle(): String

    /**
     * Lays out the top app bar actions for managing the page.
     */
    @Composable
    protected open fun TopAppBarActions() {
    }
}