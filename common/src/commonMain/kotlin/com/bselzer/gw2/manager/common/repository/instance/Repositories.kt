package com.bselzer.gw2.manager.common.repository.instance

import com.bselzer.gw2.manager.common.repository.instance.generic.ColorRepository
import com.bselzer.gw2.manager.common.repository.instance.generic.ContinentRepository
import com.bselzer.gw2.manager.common.repository.instance.generic.GuildRepository
import com.bselzer.gw2.manager.common.repository.instance.generic.OwnerRepository
import com.bselzer.gw2.manager.common.repository.instance.generic.StatusRepository
import com.bselzer.gw2.manager.common.repository.instance.generic.TileRepository
import com.bselzer.gw2.manager.common.repository.instance.generic.TranslationRepository
import com.bselzer.gw2.manager.common.repository.instance.generic.WorldRepository
import com.bselzer.gw2.manager.common.repository.instance.specialized.SelectedWorldRepository

interface Repositories {
    val color: ColorRepository
    val continent: ContinentRepository
    val guild: GuildRepository
    val owner: OwnerRepository
    val status: StatusRepository
    val tile: TileRepository
    val translation: TranslationRepository
    val world: WorldRepository
    val selectedWorld: SelectedWorldRepository
}