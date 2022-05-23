package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.state.ToggleableState
import com.bselzer.gw2.manager.common.Gw2Resources
import com.bselzer.gw2.manager.common.dependency.LocalTheme
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.main.model.cache.ClearLogic
import com.bselzer.gw2.manager.common.ui.layout.main.model.cache.ClearResources
import com.bselzer.gw2.manager.common.ui.layout.main.model.cache.ClearType
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.resource.ui.layout.icon.deleteIconInteractor
import com.bselzer.ktx.compose.resource.ui.layout.icon.triStateCheckboxIconInteractor
import com.bselzer.ktx.compose.ui.layout.iconbutton.IconButtonInteractor
import com.bselzer.ktx.compose.ui.notification.snackbar.LocalSnackbarHostState
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.resource.Resources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CacheViewModel(context: AppComponentContext) : MainViewModel(context) {
    override val title: StringDesc = Resources.strings.cache.desc()

    private val deleteAction
        @Composable
        get() = run {
            val scope = rememberCoroutineScope()
            val snackbar = LocalSnackbarHostState.current
            val notification = Resources.strings.cache_clear.desc().localized()
            IconButtonInteractor(
                enabled = selected.any(),
                icon = deleteIconInteractor(),
                onClick = {
                    // Clear the caches and then notify the user.
                    performSelected()
                    scope.launch {
                        // TODO snackbar message not getting displayed
                        snackbar.showSnackbar(notification)
                    }
                }
            )
        }

    private val selectionToggleAction
        @Composable
        get() = IconButtonInteractor(
            icon = triStateCheckboxIconInteractor(
                state = when {
                    selected.size == clears.size -> ToggleableState.On
                    selected.any() -> ToggleableState.Indeterminate
                    else -> ToggleableState.Off
                }
            ),
            onClick = ::selectionToggle
        )

    override val actions: @Composable () -> List<IconButtonInteractor> = {
        listOf(selectionToggleAction, deleteAction)
    }

    private val continentResources = ClearResources(
        type = ClearType.CONTINENT,
        image = Gw2Resources.images.gw2_gift_of_exploration,
        title = Gw2Resources.strings.continents.desc(),
        subtitle = Gw2Resources.strings.continents_description.desc(),
    )

    private val continentLogic = ClearLogic(type = ClearType.CONTINENT) {
        with(caches.gw2.continent) { clear() }
        with(caches.tile) { clear() }
    }

    private val guildResources = ClearResources(
        type = ClearType.GUILD,
        image = Gw2Resources.images.gw2_guild_commendation,
        title = Gw2Resources.strings.guilds.desc(),
        subtitle = Gw2Resources.strings.guilds_description.desc(),
    )

    private val guildLogic = ClearLogic(type = ClearType.GUILD) {
        with(caches.gw2.guild) { clear() }
    }

    private val imageResources
        @Composable
        get() = ClearResources(
            type = ClearType.IMAGE,
            image = if (LocalTheme.current == Theme.DARK) {
                Gw2Resources.images.gw2_twilight
            } else {
                Gw2Resources.images.gw2_sunrise
            },
            title = Gw2Resources.strings.images.desc(),
            subtitle = Gw2Resources.strings.images_description.desc(),
        )

    private val imageLogic = ClearLogic(type = ClearType.IMAGE) {
        with(caches.image) { clear() }
        with(caches.tile) { clear() }
    }

    private val wvwResources = ClearResources(
        type = ClearType.WVW,
        image = Gw2Resources.images.gw2_rank_dolyak,
        title = Gw2Resources.strings.wvw.desc(),
        subtitle = Gw2Resources.strings.wvw_description.desc(),
    )

    private val wvwLogic = ClearLogic(type = ClearType.WVW) {
        with(caches.gw2.wvw) { clear() }
        with(caches.gw2.world) { clear() }
    }

    val resources
        @Composable
        get() = listOf(continentResources, guildResources, imageResources, wvwResources)

    val clears = listOf(continentLogic, guildLogic, imageLogic, wvwLogic)

    private val selected: MutableMap<ClearType, ClearLogic> = mutableStateMapOf()
    fun select(type: ClearType) = selected.put(type, clears.first { clear -> clear.type == type })
    fun deselect(type: ClearType) = selected.remove(type)
    fun isSelected(type: ClearType) = selected.contains(type)

    private fun selectionToggle() {
        // If there are none selected then add them all, otherwise deselect the ones selected.
        if (selected.isEmpty()) {
            clears.forEach { clear -> selected[clear.type] = clear }
        } else {
            selected.forEach { clear -> deselect(clear.key) }
        }
    }

    private fun performSelected() {
        // Need to make a copy due to multiple threads using the collection.
        // This is to avoid emptying the list and then trying to clear it.
        clearCaches(selected.values.toList())
        selected.clear()
    }

    /**
     * Clears all of the cache [clears].
     */
    private fun clearCaches(clears: Collection<ClearLogic>) = CoroutineScope(Dispatchers.Default).launch {
        transaction {
            clears.forEach { clear -> clear.perform(this) }
            Logger.d { "Cache | Clearing ${clears.map { logic -> logic.type }}" }
        }
    }
}