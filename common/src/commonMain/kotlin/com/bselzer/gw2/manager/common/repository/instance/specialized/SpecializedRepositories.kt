package com.bselzer.gw2.manager.common.repository.instance.specialized

import com.bselzer.gw2.v2.model.wvw.match.WvwMatch

/**
 * Repositories that provide specialized data from general repositories.
 */
interface SpecializedRepositories {
    /**
     * The [WvwMatch] and map/grid data associated with the selected world.
     */
    val selectedWorld: SelectedWorldRepository
}