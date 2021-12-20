package com.bselzer.gw2.manager.android.ui.activity.wvw.page

import android.content.Intent
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import com.bselzer.gw2.manager.android.ui.activity.common.BasePage
import com.bselzer.gw2.manager.android.ui.activity.main.MainActivity
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.ktx.compose.ui.appbar.MaterialAppBar
import com.bselzer.ktx.compose.ui.appbar.UpNavigationIcon

abstract class WvwPage<State>(
    theme: Theme,
    protected val imageLoader: ImageLoader,
    private val appBarActions: @Composable RowScope.() -> Unit,
    protected val state: State
) : BasePage(theme) {
    /**
     * Lays out the top app bar.
     */
    @Composable
    protected fun TopAppBar() {
        val context = LocalContext.current
        MaterialAppBar(
            title = topAppBarTitle(),
            navigationIcon = {
                UpNavigationIcon {
                    // TODO temporary until single activity is used
                    context.startActivity(Intent(context, MainActivity::class.java))
                }
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