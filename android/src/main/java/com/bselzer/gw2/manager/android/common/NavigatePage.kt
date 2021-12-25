package com.bselzer.gw2.manager.android.common

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.bselzer.gw2.manager.common.expect.Gw2Aware
import com.bselzer.ktx.compose.ui.appbar.MaterialAppBarColumn

abstract class NavigatePage(
    aware: Gw2Aware,
    private val navigationIcon: @Composable () -> Unit,
    private val contentAlignment: Alignment = Alignment.TopStart,
) : BasePage(aware) {
    @Composable
    override fun Content() = MaterialAppBarColumn(title = title(), navigationIcon = navigationIcon, actions = appBarActions()) {
        val modifier = Modifier.fillMaxSize()
        when (background()) {
            BackgroundType.RELATIVE -> {
                RelativeBackground(modifier = modifier.background(), contentAlignment = contentAlignment) {
                    CoreContent()
                }
            }
            BackgroundType.ABSOLUTE -> {
                AbsoluteBackground(modifier = modifier.background(), contentAlignment = contentAlignment) {
                    CoreContent()
                }
            }
            BackgroundType.NONE -> {
                CoreContent()
            }
        }
    }

    /**
     * The type of background to lay out.
     */
    @Composable
    protected abstract fun background(): BackgroundType

    /**
     * The app bar title associated with this page.
     */
    @Composable
    protected abstract fun title(): String

    /**
     * Composes a modifier for the background content.
     */
    @SuppressLint("UnnecessaryComposedModifier")
    protected open fun Modifier.background(): Modifier = composed { this }

    /**
     * The relevant content to the page excluding app bar management.
     */
    @Composable
    protected abstract fun CoreContent()

    /**
     * The action buttons on the app bar.
     */
    @Composable
    protected open fun appBarActions(): @Composable RowScope.() -> Unit = {}
}