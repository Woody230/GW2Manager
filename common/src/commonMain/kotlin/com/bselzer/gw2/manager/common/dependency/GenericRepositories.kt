package com.bselzer.gw2.manager.common.dependency

import com.bselzer.gw2.manager.common.repository.instance.generic.*
import com.bselzer.gw2.manager.common.repository.instance.generic.GenericRepositories

class GenericRepositories(dependencies: RepositoryDependencies) : GenericRepositories {
    override val continent = ContinentRepository(dependencies)
    override val guild = GuildRepository(dependencies)
    override val image = ImageRepository(dependencies)
    override val tile = TileRepository(dependencies)
    override val translation = TranslationRepository(dependencies)
    override val world = WorldRepository(dependencies, translation)
}