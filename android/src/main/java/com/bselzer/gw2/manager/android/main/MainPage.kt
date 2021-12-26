package com.bselzer.gw2.manager.android.main

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.common.BasePage
import com.bselzer.gw2.manager.android.dialog.WorldSelectionDialog
import com.bselzer.gw2.manager.android.other.*
import com.bselzer.gw2.manager.android.wvw.WvwMapPage
import com.bselzer.gw2.manager.android.wvw.WvwMatchPage
import com.bselzer.gw2.manager.common.expect.Gw2Aware
import com.bselzer.gw2.manager.common.state.AppState.Companion.PageType
import com.bselzer.gw2.manager.common.state.map.WvwMapState
import com.bselzer.gw2.manager.common.state.match.WvwMatchState
import com.bselzer.ktx.compose.effect.PreRepeatedEffect
import com.bselzer.ktx.compose.ui.appbar.DrawerNavigationIcon
import com.bselzer.ktx.compose.ui.drawer.DrawerComponent
import com.bselzer.ktx.compose.ui.drawer.DrawerSection
import com.bselzer.ktx.compose.ui.drawer.MaterialDrawerContent
import com.bselzer.ktx.library.libraries
import com.bselzer.ktx.logging.Logger
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainPage(
    aware: Gw2Aware,
    private val closeApplication: () -> Unit,
) : BasePage(aware = aware) {

    @Composable
    override fun Content() {
        var selectedPage by rememberSaveable { appState.page }

        val scope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

        // Do not allow preemptive opening of the drawer before initialization from the Splash screen is complete.
        val canOpenDrawer = selectedPage != PageType.SPLASH
        val navigationIcon: @Composable () -> Unit = {
            DrawerNavigationIcon(enabled = canOpenDrawer) {
                scope.launch { drawerState.open() }
            }
        }

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
            Logger.d("Displaying page $selectedPage")
            val mapState = remember { WvwMapState(this) }
            val matchState = remember { WvwMatchState(this) }
            val libraries = LocalContext.current.libraries()
            when (selectedPage) {
                PageType.MODULE -> remember { ModulePage(aware = this, navigationIcon = navigationIcon) }
                PageType.SPLASH -> remember { SplashPage(aware = this, navigationIcon = navigationIcon) }
                PageType.ABOUT -> remember { AboutPage(aware = this, navigationIcon = navigationIcon) }
                PageType.CACHE -> remember { CachePage(aware = this, navigationIcon = navigationIcon) }
                PageType.LICENSE -> remember { LicensePage(aware = this, navigationIcon = navigationIcon, libraries = libraries) }
                PageType.SETTING -> remember { SettingsPage(aware = this, navigationIcon = navigationIcon) }
                PageType.WVW_MAP -> remember { WvwMapPage(aware = this, navigationIcon = navigationIcon, state = mapState) }
                PageType.WVW_MATCH -> remember { WvwMatchPage(aware = this, navigationIcon = navigationIcon, state = matchState) }
            }.apply { Content() }
        }

        if (remember { appState.showWorldDialog }.value) {
            WorldSelectionDialog(aware = this).Content()
        }

        // TODO maintain last refresh time instead
        PreRepeatedEffect(delay = runBlocking { wvwPref.refreshInterval.get() }) {
            appState.refreshWvwData(wvwPref.selectedWorld.get())
        }

        BackHandler(enabled = drawerState.isOpen) {
            scope.launch { drawerState.close() }
        }

        BackHandler(enabled = drawerState.isClosed) {
            // If we are on the core page then close the application.
            // Otherwise, go back to the core page.
            if (selectedPage == PageType.MODULE) {
                closeApplication()
            } else {
                selectedPage = PageType.MODULE
            }
        }
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
        DrawerComponent(iconPainter = drawable?.let { painterResource(id = drawable) }, text = stringResource(id = text)) {
            appState.page.value = page
            closeDrawer()
        }
    }
}