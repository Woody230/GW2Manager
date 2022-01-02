package com.bselzer.gw2.manager.android.main

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.common.BasePage
import com.bselzer.gw2.manager.android.dialog.WorldSelectionDialog
import com.bselzer.gw2.manager.android.other.*
import com.bselzer.gw2.manager.android.wvw.WvwMapPage
import com.bselzer.gw2.manager.android.wvw.WvwMatchPage
import com.bselzer.gw2.manager.common.state.core.DialogType
import com.bselzer.gw2.manager.common.state.core.Gw2State
import com.bselzer.gw2.manager.common.state.core.PageType
import com.bselzer.gw2.manager.common.state.map.WvwMapState
import com.bselzer.gw2.manager.common.state.match.WvwMatchState
import com.bselzer.gw2.manager.common.ui.composable.LocalState
import com.bselzer.gw2.v2.model.extension.world.WorldId
import com.bselzer.ktx.compose.effect.PostRepeatedEffect
import com.bselzer.ktx.compose.ui.appbar.DrawerNavigationIcon
import com.bselzer.ktx.compose.ui.drawer.DrawerComponent
import com.bselzer.ktx.compose.ui.drawer.DrawerSection
import com.bselzer.ktx.compose.ui.drawer.MaterialDrawerContent
import com.bselzer.ktx.library.libraries
import com.bselzer.ktx.logging.Logger
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainPage(
    private val closeApplication: () -> Unit,
) : BasePage() {

    @Composable
    override fun Gw2State.Content() {
        val currentPage by currentPage

        val scope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

        // Do not allow preemptive opening of the drawer before initialization from the Splash screen is complete.
        val canOpenDrawer = currentPage != PageType.SPLASH
        ModalDrawer(
            drawerContent = {
                DrawerContent {
                    // Close the drawer when any of its items are clicked.
                    scope.launch { drawerState.close() }
                }
            },
            drawerState = drawerState,
            gesturesEnabled = canOpenDrawer
        ) {
            // Lay out the currently selected page.
            CurrentPage {
                DrawerNavigationIcon(enabled = canOpenDrawer) {
                    scope.launch { drawerState.open() }
                }
            }
        }

        // Lay out the currently open dialog.
        CurrentDialog()

        // TODO maintain last refresh time instead
        PostRepeatedEffect(delay = runBlocking { wvwPref.refreshInterval.get() }) {
            val id = WorldId(wvwPref.selectedWorld.get())
            refreshWvwData(id)
        }

        BackHandler(enabled = drawerState.isOpen) {
            scope.launch { drawerState.close() }
        }

        BackHandler(enabled = drawerState.isClosed) {
            // If we are on the core page then close the application.
            // Otherwise, go back to the core page.
            if (currentPage == PageType.MODULE) {
                closeApplication()
            } else {
                changePage(PageType.MODULE)
            }
        }
    }

    @Composable
    private fun Gw2State.CurrentPage(navigationIcon: @Composable () -> Unit) {
        val currentPage by currentPage
        Logger.d("Displaying page $currentPage")

        val mapState = remember(this) { WvwMapState(state = this) }
        val matchState = remember(this) { WvwMatchState(state = this) }
        val libraries = LocalContext.current.libraries()
        when (currentPage) {
            PageType.MODULE -> ModulePage(navigationIcon = navigationIcon)
            PageType.SPLASH -> SplashPage(navigationIcon = navigationIcon)
            PageType.ABOUT -> AboutPage(navigationIcon = navigationIcon)
            PageType.CACHE -> CachePage(navigationIcon = navigationIcon)
            PageType.LICENSE -> LicensePage(navigationIcon = navigationIcon, libraries = libraries)
            PageType.SETTING -> SettingsPage(navigationIcon = navigationIcon)
            PageType.WVW_MAP -> WvwMapPage(navigationIcon = navigationIcon, state = mapState)
            PageType.WVW_MATCH -> WvwMatchPage(navigationIcon = navigationIcon, state = matchState)
        }.apply { Content() }
    }

    /**
     * lays out the currently selected dialog
     */
    @Composable
    private fun Gw2State.CurrentDialog() = when (currentDialog.value) {
        DialogType.WORLD_SELECTION -> WorldSelectionDialog().Content()
        else -> {}
    }

    // TODO drawer header with account name and team for the week
    /**
     * Lays out the content for the modal drawer.
     */
    @Composable
    private fun ColumnScope.DrawerContent(closeDrawer: () -> Unit) = MaterialDrawerContent(
        sections = arrayOf(
            {
                // TODO account page?
                DrawerSection(
                    header = "World vs. World",
                    components = arrayOf(
                        drawerComponent(drawable = R.drawable.gw2_rank_dolyak, text = R.string.wvw_map, page = PageType.WVW_MAP, closeDrawer = closeDrawer),
                        drawerComponent(drawable = R.drawable.gw2_rank_dolyak, text = R.string.wvw_match, page = PageType.WVW_MATCH, closeDrawer = closeDrawer)
                    )
                )
            },
            {
                DrawerSection(
                    components = arrayOf(
                        drawerComponent(drawable = R.drawable.ic_settings, text = R.string.activity_settings, page = PageType.SETTING, closeDrawer = closeDrawer),
                        drawerComponent(drawable = R.drawable.ic_cached, text = R.string.activity_cache, page = PageType.CACHE, closeDrawer = closeDrawer),
                    )
                )
            },
            {
                DrawerSection(
                components = arrayOf(
                    drawerComponent(drawable = R.drawable.ic_policy, text = R.string.activity_license, page = PageType.LICENSE, closeDrawer = closeDrawer),
                    drawerComponent(drawable = R.drawable.ic_info, text = R.string.activity_about, page = PageType.ABOUT, closeDrawer = closeDrawer)
                )
            )
        }
    ))

    /**
     * Lays out an individual drawer component for setting another page.
     */
    @Composable
    private fun drawerComponent(@DrawableRes drawable: Int?, @StringRes text: Int, page: PageType, closeDrawer: () -> Unit): @Composable ColumnScope.() -> Unit = {
        val state = LocalState.current
        DrawerComponent(iconPainter = drawable?.let { painterResource(id = drawable) }, text = stringResource(id = text)) {
            state.changePage(page)
            closeDrawer()
        }
    }
}