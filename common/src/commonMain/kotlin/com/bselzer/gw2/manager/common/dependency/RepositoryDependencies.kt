package com.bselzer.gw2.manager.common.dependency

import com.bselzer.gw2.manager.common.configuration.Configuration
import org.kodein.db.DB

interface RepositoryDependencies {
    val database: DB
    val preferences: Preferences
    val clients: Clients
    val configuration: Configuration
}