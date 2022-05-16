package com.bselzer.gw2.manager.common.dependency

import com.bselzer.gw2.manager.common.repository.WorldRepository
import com.bselzer.gw2.manager.common.repository.WvwRepository

data class Repositories(
    val world: WorldRepository,
    val wvw: WvwRepository
)