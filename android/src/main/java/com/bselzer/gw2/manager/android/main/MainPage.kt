package com.bselzer.gw2.manager.android.main

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.common.BasePage
import com.bselzer.gw2.manager.android.main.MainPage.PageType.*
import com.bselzer.gw2.manager.android.other.AboutPage
import com.bselzer.gw2.manager.android.other.CachePage
import com.bselzer.gw2.manager.android.other.LicensePage
import com.bselzer.gw2.manager.android.other.SettingsPage
import com.bselzer.gw2.manager.android.wvw.WvwPage
import com.bselzer.gw2.manager.common.expect.Gw2Aware
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.ktx.compose.ui.appbar.DrawerNavigationIcon
import com.bselzer.ktx.compose.ui.drawer.DrawerComponent
import com.bselzer.ktx.compose.ui.drawer.DrawerSection
import com.bselzer.ktx.compose.ui.drawer.MaterialDrawerContent
import com.bselzer.ktx.function.core.hasInternet
import com.bselzer.ktx.function.objects.isOneOf
import com.bselzer.ktx.library.libraries
import com.bselzer.ktx.logging.Logger
import kotlinx.coroutines.launch

class MainPage(
    aware: Gw2Aware,
    private val closeApplication: () -> Unit,
) : BasePage(aware = aware) {
    private val selectedPage = mutableStateOf(SPLASH)
    private val wvwPage = mutableStateOf(WvwPage.PageType.MENU)

    enum class PageType {
        MENU,
        SPLASH,
        ABOUT,
        CACHE,
        LICENSE,
        SETTING,
        WVW
    }

    @Composable
    override fun Content() {
        var selectedPage by rememberSaveable { selectedPage }
        val navigateUp: () -> Unit = {
            selectedPage = MENU
            Logger.d("Changing main page to $selectedPage")
        }

        Logger.d("Displaying main page $selectedPage")
        when (selectedPage) {
            MENU -> MainMenu()
            SPLASH -> Splash()
            ABOUT -> AboutPage(aware = this, navigateUp).Content()
            CACHE -> CachePage(aware = this, navigateUp).Content()
            LICENSE -> LicensePage(aware = this, navigateUp, LocalContext.current.libraries()).Content()
            SETTING -> SettingsPage(aware = this, navigateUp).Content()
            WVW -> WvwPage(aware = this, navigateUp = navigateUp, backEnabled = selectedPage == WVW, selectedPage = wvwPage).Content()
        }

        // Let the WvW page use its own back handler.
        // TODO better screen management
        BackHandler(enabled = selectedPage.isOneOf(MENU, SPLASH), onBack = closeApplication)
        BackHandler(enabled = selectedPage.isOneOf(ABOUT, CACHE, LICENSE, SETTING), onBack = navigateUp)
    }

    /**
     * Lays out all of the buttons within the main menu.
     */
    @Composable
    private fun MainMenu() {
        val scope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        ModalDrawer(drawerContent = { DrawerContent() }, drawerState = drawerState) {
            AbsoluteBackgroundContent(
                title = stringResource(id = R.string.app_name),
                contentAlignment = Alignment.Center,
                navigationIcon = {
                    DrawerNavigationIcon {
                        scope.launch { drawerState.open() }
                    }
                }
            ) {
                // TODO modules: which worlds are assigned to each side for currently selected world, world selection, overview of WvW data, etc
            }
        }

        BackHandler(enabled = drawerState.isOpen) {
            scope.launch { drawerState.close() }
        }
    }

    // TODO drawer header
    /**
     * Lays out the content for the modal drawer.
     */
    @Composable
    private fun ColumnScope.DrawerContent() = MaterialDrawerContent(sections = arrayOf(
        {
            // TODO account page?
            DrawerSection(
                header = "World vs. World",
                components = arrayOf(
                    drawerComponent(drawable = R.drawable.gw2_rank_dolyak, text = R.string.wvw_map, page = WvwPage.PageType.MAP),
                    drawerComponent(drawable = R.drawable.gw2_rank_dolyak, text = R.string.wvw_match, page = WvwPage.PageType.MATCH)
                )
            )
        },
        {
            DrawerSection(
                components = arrayOf(
                    drawerComponent(drawable = R.drawable.ic_settings, text = R.string.activity_settings, page = SETTING),
                    drawerComponent(drawable = R.drawable.ic_cached, text = R.string.activity_cache, page = CACHE),
                )
            )
        },
        {
            DrawerSection(
                components = arrayOf(
                    drawerComponent(drawable = R.drawable.ic_policy, text = R.string.activity_license, page = LICENSE),
                    drawerComponent(drawable = R.drawable.ic_info, text = R.string.activity_about, page = ABOUT)
                )
            )
        }
    ))

    /**
     * Lays out an individual drawer component for setting another page.
     */
    @Composable
    private fun drawerComponent(@DrawableRes drawable: Int?, @StringRes text: Int, page: PageType, onClick: () -> Unit = {}): @Composable ColumnScope.() -> Unit = {
        DrawerComponent(iconPainter = drawable?.let { painterResource(id = drawable) }, text = stringResource(id = text)) {
            onClick()
            selectedPage.value = page
            Logger.d("Changing main page to ${selectedPage.value}")
        }
    }

    /**
     * Lays out an individual drawer component for setting another page.
     */
    @Composable
    private fun drawerComponent(@DrawableRes drawable: Int?, @StringRes text: Int, page: WvwPage.PageType): @Composable ColumnScope.() -> Unit = drawerComponent(
        drawable = drawable,
        text = text,
        page = WVW,
    ) {
        wvwPage.value = page
        Logger.d("Changing World vs. World page to ${wvwPage.value}")
    }

    /**
     * Lays out the splash screen to allow for preprocessing.
     */
    @Composable
    private fun Splash() = AbsoluteBackgroundContent(
        title = stringResource(id = R.string.app_name),
        contentAlignment = Alignment.Center
    ) {
        val (description, setDescription) = remember { mutableStateOf("") }
        Downloading(description)
        RetrieveData(setDescription)
    }

    /**
     * Lays out the download indicator.
     */
    @Composable
    private fun Downloading(description: String) = RelativeBackgroundColumn(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Text(
            text = description,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        ProgressIndicator()
    }

    /**
     * Load initial data from the API.
     */
    @Composable
    private fun RetrieveData(setDescription: (String) -> Unit) {
        val initialTheme = if (isSystemInDarkTheme()) Theme.DARK else Theme.LIGHT
        val context = LocalContext.current
        var selectedPage by selectedPage
        LaunchedEffect(selectedPage) {
            fun finishedDownloading() {
                selectedPage = MENU
            }

            commonPref.theme.initialize(initialTheme)

            if (!context.hasInternet()) {
                finishedDownloading()
                return@LaunchedEffect
            }

            setDescription("Build Number")
            val newId = gw2Client.build.buildId()
            val buildNumber = commonPref.buildNumber
            if (newId > buildNumber.get()) {
                buildNumber.set(newId)
            }

            finishedDownloading()
        }
    }
}