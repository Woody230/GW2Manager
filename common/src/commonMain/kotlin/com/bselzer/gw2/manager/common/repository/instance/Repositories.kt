package com.bselzer.gw2.manager.common.repository.instance

import com.bselzer.gw2.manager.common.repository.instance.generic.*
import com.bselzer.gw2.manager.common.repository.instance.specialized.SelectedWorldRepository

interface Repositories {
    val continent: ContinentRepository
    val guild: GuildRepository
    val image: ImageRepository
    val tile: TileRepository
    val translation: TranslationRepository
    val world: WorldRepository
    val selectedWorld: SelectedWorldRepository
}