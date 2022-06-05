package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.state.ToggleableState
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.dependency.LocalTheme
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.GeneralAction
import com.bselzer.gw2.manager.common.ui.layout.main.model.cache.ClearLogic
import com.bselzer.gw2.manager.common.ui.layout.main.model.cache.ClearResources
import com.bselzer.gw2.manager.common.ui.layout.main.model.cache.ClearType
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.gw2.v2.cache.operation.clearContinent
import com.bselzer.gw2.v2.cache.operation.clearGuild
import com.bselzer.gw2.v2.cache.operation.clearWvw
import com.bselzer.gw2.v2.tile.cache.operation.clearTile
import com.bselzer.ktx.compose.image.cache.operation.clearImage
import com.bselzer.ktx.compose.resource.ui.layout.icon.deleteIconInteractor
import com.bselzer.ktx.compose.resource.ui.layout.icon.triStateCheckboxIconInteractor
import com.bselzer.ktx.kodein.db.transaction.transaction
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.resource.KtxResources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CacheViewModel(context: AppComponentContext) : MainViewModel(context) {
    override val title: StringDesc = KtxResources.strings.cache.desc()

    private val deleteAction
        get() = GeneralAction(
            enabled = selected.any(),
            notification = KtxResources.strings.cache_clear.desc(),
            icon = { deleteIconInteractor() },
            onClick = { clearSelected() }
        )

    private val selectionToggleAction
        get() = GeneralAction(
            onClick = { selectionToggle() },
            icon = {
                triStateCheckboxIconInteractor(
                    state = when {
                        selected.size == clears.size -> ToggleableState.On
                        selected.any() -> ToggleableState.Indeterminate
                        else -> ToggleableState.Off
                    }
                )
            },
        )

    override val actions
        get() = listOf(selectionToggleAction, deleteAction)

    private val continentResources = ClearResources(
        type = ClearType.CONTINENT,
        image = AppResources.images.gw2_gift_of_exploration,
        title = AppResources.strings.continents.desc(),
        subtitle = AppResources.strings.continents_description.desc(),
    )

    private val continentLogic = ClearLogic(type = ClearType.CONTINENT) {
        clearContinent()
        clearTile()
    }

    private val guildResources = ClearResources(
        type = ClearType.GUILD,
        image = AppResources.images.gw2_guild_commendation,
        title = AppResources.strings.guilds.desc(),
        subtitle = AppResources.strings.guilds_description.desc(),
    )

    private val guildLogic = ClearLogic(type = ClearType.GUILD) {
        clearGuild()
    }

    private val imageResources
        @Composable
        get() = ClearResources(
            type = ClearType.IMAGE,
            image = if (LocalTheme.current == Theme.DARK) {
                AppResources.images.gw2_twilight
            } else {
                AppResources.images.gw2_sunrise
            },
            title = AppResources.strings.images.desc(),
            subtitle = AppResources.strings.images_description.desc(),
        )

    private val imageLogic = ClearLogic(type = ClearType.IMAGE) {
        clearImage()
        clearTile()
    }

    private val wvwResources = ClearResources(
        type = ClearType.WVW,
        image = AppResources.images.gw2_rank_dolyak,
        title = AppResources.strings.wvw.desc(),
        subtitle = AppResources.strings.wvw_description.desc(),
    )

    private val wvwLogic = ClearLogic(type = ClearType.WVW) {
        clearWvw()
    }

    val resources
        @Composable
        get() = listOf(continentResources, guildResources, imageResources, wvwResources)

    val clears
        get() = listOf(continentLogic, guildLogic, imageLogic, wvwLogic)

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

    private fun clearSelected() {
        // Need to make a copy due to multiple threads using the collection.
        // This is to avoid emptying the list and then trying to clear it.
        clearCaches(selected.values.toList())
        selected.clear()
    }

    /**
     * Clears all of the cache [clears].
     */
    private fun clearCaches(clears: Collection<ClearLogic>) = CoroutineScope(Dispatchers.Default).launch {
        database.transaction().use {
            clears.forEach { clear -> clear.perform(this) }
            Logger.d { "Cache | Clearing ${clears.map { logic -> logic.type }}" }
        }
    }
}