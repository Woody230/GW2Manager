package com.bselzer.gw2.manager.common.repository.instance.specialized

import com.bselzer.gw2.manager.common.repository.instance.generic.WorldRepository

interface SelectedWorldRepositories {
    val world: WorldRepository
    val match: WvwMatchRepository
    val map: MapRepository
}