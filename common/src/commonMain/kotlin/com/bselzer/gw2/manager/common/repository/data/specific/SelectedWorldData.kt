package com.bselzer.gw2.manager.common.repository.data.specific

import com.bselzer.gw2.v2.model.continent.Continent
import com.bselzer.gw2.v2.model.continent.floor.Floor
import com.bselzer.gw2.v2.model.continent.map.ContinentMap
import com.bselzer.gw2.v2.model.map.MapId
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.world.WorldId
import com.bselzer.gw2.v2.model.wvw.map.WvwMap

interface SelectedWorldData : MapData, MatchData {
    /**
     * The id of the world currently being used to provide the other data.
     */
    val worldId: WorldId?

    /**
     * The world associated with the [worldId].
     */
    val world: World?

    /**
     * The continent for the current match.
     */
    val continent: Continent?

    /**
     * The floor within the [continent] for the current match.
     */
    val floor: Floor?

    /**
     * The maps within the regions of the [floor] for the current match.
     */
    val continentMaps: Map<MapId, ContinentMap>

    /**
     * The maps defined in the current match mapped to their full details.
     */
    val matchMaps: Map<WvwMap, com.bselzer.gw2.v2.model.map.Map>

    /**
     * Updates the grid to the new [zoom] level with the current match's map.
     */
    suspend fun updateZoom(zoom: Int)

    /**
     * Refreshes the [MapData] and [MatchData] regardless of when the last refresh occurred.
     */
    suspend fun forceRefresh()
}