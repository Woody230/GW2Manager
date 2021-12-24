package com.bselzer.gw2.manager.android.ui.activity.wvw.state

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.WvwMapState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.match.WvwMatchState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.selected.WvwSelectedState
import com.bselzer.gw2.manager.common.configuration.wvw.Wvw
import com.bselzer.gw2.v2.emblem.client.EmblemClient
import com.bselzer.gw2.v2.model.continent.Continent
import com.bselzer.gw2.v2.model.continent.ContinentFloor
import com.bselzer.gw2.v2.model.enumeration.wvw.ObjectiveOwner
import com.bselzer.gw2.v2.model.guild.Guild
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgrade
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.gw2.v2.tile.model.response.Tile
import com.bselzer.gw2.v2.tile.model.response.TileGrid

data class WvwState(
    val configuration: Wvw,
    private val emblemClient: EmblemClient,
    val worlds: MutableState<Collection<World>> = mutableStateOf(emptyList()),
    val match: MutableState<WvwMatch?> = mutableStateOf(null),
    val objectives: MutableState<Collection<WvwObjective>> = mutableStateOf(emptyList()),
    val upgrades: MutableState<Map<Int, WvwUpgrade>> = mutableStateOf(emptyMap()),
    val guildUpgrades: MutableState<Map<Int, GuildUpgrade>> = mutableStateOf(emptyMap()),
    val guilds: SnapshotStateMap<String, Guild> = mutableStateMapOf(),
    val selectedObjective: MutableState<WvwObjective?> = mutableStateOf(null),
    val zoom: MutableState<Int> = mutableStateOf(0),
    val continent: MutableState<Continent?> = mutableStateOf(null),
    val floor: MutableState<ContinentFloor?> = mutableStateOf(null),
    val grid: MutableState<TileGrid> = mutableStateOf(TileGrid()),
    val tileContent: SnapshotStateMap<Tile, ByteArray> = mutableStateMapOf(),
    val enableScrollToRegion: MutableState<Boolean> = mutableStateOf(true),
    val horizontalScroll: ScrollState = ScrollState(0),
    val verticalScroll: ScrollState = ScrollState(0),
    val owners: Collection<ObjectiveOwner> = listOf(ObjectiveOwner.BLUE, ObjectiveOwner.GREEN, ObjectiveOwner.RED)
) {
    /**
     * Remembers the state of map related information.
     */
    @Composable
    fun rememberMap(): WvwMapState = WvwMapState(
        configuration = configuration,
        match = remember { match },
        objectives = remember { objectives },
        selectedObjective = remember { selectedObjective },
        upgrades = remember { upgrades },
        guildUpgrades = remember { guildUpgrades },
        zoom = remember { zoom },
        continent = remember { continent },
        floor = remember { floor },
        grid = remember { grid },
        tileContent = remember { tileContent },
        enableScrollToRegion = remember { enableScrollToRegion },

        // Applying rememberSaveable like rememberScrollState() does.
        horizontalScroll = rememberSaveable(saver = ScrollState.Saver) { horizontalScroll },
        verticalScroll = rememberSaveable(saver = ScrollState.Saver) { verticalScroll }
    )

    /**
     * Remembers the state of match related information.
     */
    @Composable
    fun rememberMatch() = WvwMatchState(
        configuration = configuration,
        match = remember { match },
        worlds = remember { worlds },
        owners = remember { owners }
    )

    /**
     * Remembers the state of selected objective related information.
     */
    @Composable
    fun rememberSelected() = WvwSelectedState(
        configuration = configuration,
        emblemClient = emblemClient,
        match = remember { match },
        selectedObjective = remember { selectedObjective },
        worlds = remember { worlds },
        upgrades = remember { upgrades },
        guilds = remember { guilds }
    )
}