package com.bselzer.gw2.manager.common.dependency

import com.bselzer.gw2.manager.common.configuration.Configuration

interface RepositoryDependencies {
    val caches: Caches
    val clients: Clients
    val preferences: Preferences
    val configuration: Configuration
}