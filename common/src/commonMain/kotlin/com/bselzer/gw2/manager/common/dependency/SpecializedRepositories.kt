package com.bselzer.gw2.manager.common.dependency

import com.bselzer.gw2.manager.common.repository.instance.Repositories
import com.bselzer.gw2.manager.common.repository.instance.generic.WorldRepository
import com.bselzer.gw2.manager.common.repository.instance.specialized.*
import com.bselzer.gw2.manager.common.repository.instance.specialized.SpecializedRepositories

class SpecializedRepositories(
    dependencies: RepositoryDependencies,
    generic: GenericRepositories
) : Repositories, SpecializedRepositories, com.bselzer.gw2.manager.common.repository.instance.generic.GenericRepositories by generic {
    private val selectedMatch = WvwMatchRepository(dependencies, generic)
    private val selectedMap = MapRepository(dependencies, generic)
    private val selectedWorldRepositories = object : SelectedWorldRepositories {
        override val match: WvwMatchRepository = selectedMatch
        override val map: MapRepository = selectedMap
        override val world: WorldRepository = generic.world
    }

    override val selectedWorld: SelectedWorldRepository = SelectedWorldRepository(dependencies, selectedWorldRepositories)
}