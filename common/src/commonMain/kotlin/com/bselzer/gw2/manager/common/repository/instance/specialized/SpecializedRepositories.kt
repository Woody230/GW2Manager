package com.bselzer.gw2.manager.common.repository.instance.specialized

import com.bselzer.gw2.v2.model.wvw.match.WvwMatch

/**
 * Repositories that provide specialized data from general repositories.
 */
interface SpecializedRepositories {
    /**
     * The [WvwMatch] data associated with the selected world.
     */
    val selectedMatch: WvwMatchRepository

    /**
     * The map and grid data associated with the selected world.
     */
    val selectedMap: MapRepository
}