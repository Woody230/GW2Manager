package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import com.bselzer.gw2.manager.common.Gw2Resources
import com.bselzer.gw2.manager.common.dependency.LocalTheme
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.main.model.cache.CacheClear
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.ktx.compose.resource.ui.layout.icon.deleteIconInteractor
import com.bselzer.ktx.compose.ui.layout.iconbutton.IconButtonInteractor
import com.bselzer.ktx.compose.ui.notification.snackbar.LocalSnackbarHostState
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.resource.Resources
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CacheViewModel(context: AppComponentContext) : MainViewModel(context) {
    override val title: StringDesc = Resources.strings.cache.desc()

    override val actions: @Composable () -> List<IconButtonInteractor> = {
        val scope = rememberCoroutineScope()
        val snackbar = LocalSnackbarHostState.current
        val notification = Resources.strings.cache_clear.desc().localized()
        listOf(
            IconButtonInteractor(
                icon = deleteIconInteractor(),
                onClick = {
                    // Clear the caches and then notify the user.
                    performSelected()
                    scope.launch {
                        snackbar.showSnackbar(notification)
                    }
                }
            )
        )
    }

    val continent = CacheClear(
        image = Gw2Resources.images.gw2_gift_of_exploration,
        title = Gw2Resources.strings.continents.desc(),
        subtitle = Gw2Resources.strings.continents_description.desc(),
        perform = {
            with(caches.gw2.continent) { clear() }
            with(caches.tile) { clear() }
        }
    )

    val guild = CacheClear(
        image = Gw2Resources.images.gw2_guild_commendation,
        title = Gw2Resources.strings.guilds.desc(),
        subtitle = Gw2Resources.strings.guilds_description.desc(),
        perform = {
            with(caches.gw2.guild) { clear() }
        }
    )

    val image
        @Composable
        get() = CacheClear(
            image = if (LocalTheme.current == Theme.DARK) {
                Gw2Resources.images.gw2_twilight
            } else {
                Gw2Resources.images.gw2_sunrise
            },
            title = Gw2Resources.strings.images.desc(),
            subtitle = Gw2Resources.strings.images_description.desc(),
            perform = {
                with(caches.image) { clear() }
                with(caches.tile) { clear() }
            }
        )

    val wvw = CacheClear(
        image = Gw2Resources.images.gw2_rank_dolyak,
        title = Gw2Resources.strings.wvw.desc(),
        subtitle = Gw2Resources.strings.wvw_description.desc(),
        perform = {
            with(caches.gw2.wvw) { clear() }
        }
    )

    private val _selected: MutableList<CacheClear> = mutableStateListOf()
    val selected: List<CacheClear> = _selected
    fun select(cache: CacheClear) = _selected.add(cache)
    fun deselect(cache: CacheClear) = _selected.remove(cache)
    fun performSelected() {
        clearCaches(_selected.toList())
        _selected.clear()
    }

    /**
     * Clears all of the [caches].
     */
    private fun clearCaches(caches: Collection<CacheClear>) = CoroutineScope(Dispatchers.Default).launch {
        transaction {
            caches.forEach { cache -> cache.perform(this) }
            Logger.d { "Cache | Clearing ${caches.size}" }
        }
    }
}