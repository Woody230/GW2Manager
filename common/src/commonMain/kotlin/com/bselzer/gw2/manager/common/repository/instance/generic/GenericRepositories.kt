package com.bselzer.gw2.manager.common.repository.instance.generic

/**
 * Repositories independent of each other.
 */
interface GenericRepositories {
    val continent: ContinentRepository
    val guild: GuildRepository
    val tile: TileRepository
    val world: WorldRepository
}