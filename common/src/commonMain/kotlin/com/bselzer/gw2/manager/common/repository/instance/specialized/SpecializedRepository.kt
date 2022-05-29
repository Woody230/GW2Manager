package com.bselzer.gw2.manager.common.repository.instance.specialized

import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.repository.instance.AppRepository
import com.bselzer.gw2.manager.common.repository.instance.generic.GenericRepositories

abstract class SpecializedRepository(dependencies: RepositoryDependencies, protected val repositories: GenericRepositories) : AppRepository(dependencies)