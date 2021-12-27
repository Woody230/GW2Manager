package com.bselzer.gw2.manager.common.state.core

import androidx.compose.runtime.State
import com.bselzer.gw2.v2.model.extension.world.WorldId
import com.bselzer.gw2.v2.model.guild.Guild
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgrade
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade

/**
 * The state of common/shared data.
 */
interface DataState {
    /**
     * The match associated with the selected world.
     */
    val worldMatch: State<WvwMatch?>

    /**
     * The objectives associated with the selected world match.
     */
    val worldObjectives: State<Collection<WvwObjective>>

    /**
     * All possible worlds to choose from.
     */
    val worlds: Map<Int, World>

    /**
     * A mapping of the id of the World vs. World upgrade to the upgrade itself.
     */
    val upgrades: Map<Int, WvwUpgrade>

    /**
     * A mapping of the id of the guild upgrade to the upgrade itself.
     */
    val guildUpgrades: Map<Int, GuildUpgrade>

    /**
     * A mapping of the id of the guild to the guild itself.
     */
    val guilds: Map<String, Guild>

    /**
     * Initializes World vs. World data with as much cached information as possible using the chosen world.
     */
    suspend fun initializeWvwData()

    /**
     * Performs a full refresh of common World vs. World data and the data specific to the chosen world.
     */
    suspend fun refreshWvwData(worldId: WorldId)

    /**
     * Refreshes the data associated with the guild with the given [id].
     */
    suspend fun refreshGuild(id: String?)
}