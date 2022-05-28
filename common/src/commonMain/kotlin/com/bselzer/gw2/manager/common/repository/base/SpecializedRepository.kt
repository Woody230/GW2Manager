package com.bselzer.gw2.manager.common.repository.base

import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies

abstract class SpecializedRepository(dependencies: RepositoryDependencies, protected val repositories: GenericRepositories) : AppRepository(dependencies)