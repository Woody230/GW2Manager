package com.bselzer.gw2.manager.common.repository.base

import com.bselzer.gw2.manager.common.repository.instance.ContinentRepository
import com.bselzer.gw2.manager.common.repository.instance.GuildRepository
import com.bselzer.gw2.manager.common.repository.instance.TileRepository
import com.bselzer.gw2.manager.common.repository.instance.WorldRepository

/**
 * Repositories independent of each other.
 */
interface GenericRepositories {
    val continent: ContinentRepository
    val guild: GuildRepository
    val tile: TileRepository
    val world: WorldRepository
}