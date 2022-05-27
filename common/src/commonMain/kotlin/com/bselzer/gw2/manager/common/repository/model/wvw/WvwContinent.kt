package com.bselzer.gw2.manager.common.repository.model.wvw

import com.bselzer.gw2.v2.model.continent.Continent
import com.bselzer.gw2.v2.model.continent.floor.Floor

data class WvwContinent(
    val continent: Continent,
    val floor: Floor,
)