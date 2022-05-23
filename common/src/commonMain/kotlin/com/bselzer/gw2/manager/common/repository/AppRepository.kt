package com.bselzer.gw2.manager.common.repository

import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies

abstract class AppRepository(dependencies: RepositoryDependencies) : RepositoryDependencies by dependencies