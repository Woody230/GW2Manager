package com.bselzer.gw2.manager.common.state.core

import androidx.compose.runtime.*
import com.bselzer.gw2.asset.cdn.client.AssetCdnClient
import com.bselzer.gw2.manager.common.configuration.Configuration
import com.bselzer.gw2.manager.common.expect.Gw2Aware
import com.bselzer.gw2.manager.common.preference.CommonPreference
import com.bselzer.gw2.manager.common.preference.WvwPreference
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.gw2.v2.cache.instance.GuildCache
import com.bselzer.gw2.v2.cache.instance.WorldCache
import com.bselzer.gw2.v2.cache.instance.WvwCache
import com.bselzer.gw2.v2.cache.provider.Gw2CacheProvider
import com.bselzer.gw2.v2.client.client.Gw2Client
import com.bselzer.gw2.v2.emblem.client.EmblemClient
import com.bselzer.gw2.v2.model.extension.world.WorldId
import com.bselzer.gw2.v2.model.extension.wvw.objective
import com.bselzer.gw2.v2.model.guild.Guild
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgrade
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.gw2.v2.tile.cache.instance.TileCache
import com.bselzer.gw2.v2.tile.client.TileClient
import com.bselzer.ktx.compose.image.cache.instance.ImageCache
import com.bselzer.ktx.logging.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kodein.db.DB
import org.kodein.di.DI

class AppState(
    private val aware: Gw2Aware
) : Gw2State {
    // region Aware

    override val di: DI = aware.di
    override val database: DB = aware.database
    override val gw2Client: Gw2Client = aware.gw2Client
    override val gw2Cache: Gw2CacheProvider = aware.gw2Cache
    override val tileClient: TileClient = aware.tileClient
    override val tileCache: TileCache = aware.tileCache
    override val imageCache: ImageCache = aware.imageCache
    override val emblemClient: EmblemClient = aware.emblemClient
    override val assetCdnClient: AssetCdnClient = aware.assetCdnClient
    override val configuration: Configuration = aware.configuration
    override val commonPref: CommonPreference = aware.commonPref
    override val wvwPref: WvwPreference = aware.wvwPref

    @Composable
    override fun Content(content: @Composable () -> Unit) = aware.Content(content)

    // endregion Aware

    // region ComposeState

    override val currentPage = mutableStateOf(PageType.SPLASH)
    override val splashRedirectPage = mutableStateOf<PageType?>(null)
    override val currentDialog = mutableStateOf<DialogType?>(null)

    override fun changePage(page: PageType) {
        Logger.d("Changing the current page from ${currentPage.value} to $page.")
        currentPage.value = page
        if (page != PageType.SPLASH) {
            splashRedirectPage.value = page
        }
    }

    override fun changeDialog(dialog: DialogType) {
        Logger.d("Changing the current dialog from ${currentDialog.value} to $dialog.")
        currentDialog.value = dialog
    }

    override fun clearDialog() {
        Logger.d("Clearing the current dialog from ${currentDialog.value}.")
        currentDialog.value = null
    }

    override suspend fun changeTheme(theme: Theme) = withContext(Dispatchers.IO) {
        commonPref.theme.set(theme)
    }

    // endregion ComposeState

    // region DataState

    override val worldMatch = mutableStateOf<WvwMatch?>(null)
    override val worldObjectives = mutableStateOf<Collection<WvwObjective>>(emptyList())
    override val worlds = mutableStateMapOf<Int, World>()
    override val upgrades = mutableStateMapOf<Int, WvwUpgrade>()
    override val guildUpgrades = mutableStateMapOf<Int, GuildUpgrade>()
    override val guilds = mutableStateMapOf<String, Guild>()

    override suspend fun initializeWvwData(): Unit = withContext(Dispatchers.IO) {
        val worldId = WorldId(wvwPref.selectedWorld.get())
        Logger.d("Initialization of WvW data for world ${worldId.value}.")

        gw2Cache.lockedInstance {
            get<WorldCache>().findWorlds().forEach { world ->
                worlds[world.id] = world
            }

            get<WvwCache>().apply {
                put(findMatch(worldId))

                // Set up all the configured guild upgrades since there is no direct way to know what upgrades are associated with each tier.
                val improvementIds = configuration.wvw.objectives.guildUpgrades.improvements.flatMap { improvement -> improvement.upgrades.map { upgrade -> upgrade.id } }
                val tacticIds = configuration.wvw.objectives.guildUpgrades.tactics.flatMap { tactic -> tactic.upgrades.map { upgrade -> upgrade.id } }
                findGuildUpgrades(ids = improvementIds + tacticIds).forEach { guildUpgrade -> guildUpgrades[guildUpgrade.id] = guildUpgrade }
            }
        }
    }

    override suspend fun refreshWvwData(worldId: WorldId) = withContext(Dispatchers.IO) {
        val selectedWorld = worldId.value
        Logger.d("Refreshing WvW data for world ${selectedWorld}.")

        gw2Cache.lockedInstance {
            get<WorldCache>().findWorlds().forEach { world ->
                worlds[world.id] = world
            }

            // Need the world to be able to get the associated match.
            if (selectedWorld <= 0) {
                changeDialog(DialogType.WORLD_SELECTION)
                return@lockedInstance
            }

            get<WvwCache>().apply {
                put(gw2Client.wvw.match(selectedWorld))
            }
        }
    }

    override suspend fun refreshGuild(id: String?) = withContext(Dispatchers.IO) {
        if (id.isNullOrBlank()) {
            // Skip refreshing bad ids.
            return@withContext
        }

        guilds[id] = gw2Cache.lockedInstance {
            get<GuildCache>().getGuild(id)
        }
    }

    /**
     * Puts the match, and its associated objectives/upgrade information.
     */
    private suspend fun WvwCache.put(match: WvwMatch) {
        putMatch(match)
        worldMatch.value = match

        val objectives = findObjectives(match)
        worldObjectives.value = objectives

        findUpgrades(objectives).forEach { upgrade ->
            upgrades[upgrade.id] = upgrade
        }
        findGuildUpgrades(objectives.mapNotNull { objective -> match.objective(objective) }).forEach { guildUpgrade ->
            guildUpgrades[guildUpgrade.id] = guildUpgrade
        }
    }

    // endregion DataState
}