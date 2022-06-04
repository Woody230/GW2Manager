package com.bselzer.gw2.manager.common.repository.instance.generic

/**
 * Repositories independent of each other.
 */
interface GenericRepositories {
    val continent: ContinentRepository
    val guild: GuildRepository
    val image: ImageRepository
    val tile: TileRepository
    val translation: TranslationRepository
    val world: WorldRepository
}