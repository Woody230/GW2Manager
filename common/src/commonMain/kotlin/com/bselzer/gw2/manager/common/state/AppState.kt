package com.bselzer.gw2.manager.common.state

import androidx.compose.runtime.*
import com.bselzer.gw2.manager.common.expect.Gw2Aware
import com.bselzer.gw2.v2.cache.instance.GuildCache
import com.bselzer.gw2.v2.cache.instance.WorldCache
import com.bselzer.gw2.v2.cache.instance.WvwCache
import com.bselzer.gw2.v2.model.extension.world.WorldId
import com.bselzer.gw2.v2.model.extension.wvw.objective
import com.bselzer.gw2.v2.model.guild.Guild
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgrade
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.ktx.logging.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppState(
    aware: Gw2Aware
) : Gw2Aware by aware {

    /**
     * The type of page to lay out.
     */
    val page = mutableStateOf(PageType.SPLASH)

    /**
     * The match associated with the selected world.
     */
    val match = mutableStateOf<WvwMatch?>(null)

    /**
     * The objectives associated with the selected world match.
     */
    val objectives = mutableStateOf<Collection<WvwObjective>>(emptyList())

    /**
     * All possible worlds to choose from.
     */
    val worlds = mutableStateMapOf<Int, World>()

    val upgrades = mutableStateMapOf<Int, WvwUpgrade>()
    val guildUpgrades = mutableStateMapOf<Int, GuildUpgrade>()
    val guilds = mutableStateMapOf<String, Guild>()
    val showWorldDialog = mutableStateOf(false)

    companion object {
        enum class PageType {
            SPLASH,
            MODULE,
            ABOUT,
            CACHE,
            LICENSE,
            SETTING,
            WVW_MAP,
            WVW_MATCH,
            // TODO WVW_MATCHES (who vs who)
        }
    }

    // region Refresh

    /**
     * Sets the data associated with the initial match.
     */
    suspend fun initialWvwData() = withContext(Dispatchers.IO) {
        val worldId = WorldId(wvwPref.selectedWorld.get())
        Logger.d("Initialization of WvW data for world ${worldId.value}.")

        gw2Cache.lockedInstance {
            get<WorldCache>().findWorlds().forEach { world ->
                appState.worlds[world.id] = world
            }

            get<WvwCache>().apply {
                put(findMatch(worldId))
            }
        }
    }

    /**
     * Refreshes the common WvW data and any data specific to the user's chosen world.
     */
    suspend fun refreshWvwData(selectedWorld: Int) = withContext(Dispatchers.IO) {
        Logger.d("Refreshing WvW data for world ${selectedWorld}.")

        gw2Cache.lockedInstance {
            get<WorldCache>().findWorlds().forEach { world ->
                appState.worlds[world.id] = world
            }

            // Need the world to be able to get the associated match.
            if (selectedWorld <= 0) {
                showWorldDialog.value = true
                return@lockedInstance
            }

            get<WvwCache>().apply {
                put(gw2Client.wvw.match(selectedWorld))
            }
        }
    }

    /**
     * Refreshes the guild data for the [id].
     */
    suspend fun refreshGuild(id: String?) = withContext(Dispatchers.IO) {
        if (id.isNullOrBlank()) {
            // Skip refreshing bad ids.
            return@withContext
        }

        guilds[id] = gw2Cache.instance {
            get<GuildCache>().getGuild(id)
        }
    }

    /**
     * Puts the match, objective, and upgrade information.
     */
    private suspend fun WvwCache.put(match: WvwMatch) {
        putMatch(match)
        appState.match.value = match

        val objectives = findObjectives(match)
        appState.objectives.value = objectives

        findUpgrades(objectives).forEach { upgrade ->
            appState.upgrades[upgrade.id] = upgrade
        }
        findGuildUpgrades(objectives.mapNotNull { objective -> match.objective(objective) }).forEach { guildUpgrade ->
            appState.guildUpgrades[guildUpgrade.id] = guildUpgrade
        }
    }

    // endregion Refresh
}