package com.bselzer.gw2.manager.common.dependency

import com.bselzer.gw2.manager.common.configuration.Configuration
import kotlinx.coroutines.CoroutineScope

interface RepositoryDependencies {
    val clients: Clients
    val configuration: Configuration
    val storage: Storage
    val preferences: Preferences
    val scope: CoroutineScope
}