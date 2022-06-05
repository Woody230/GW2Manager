package com.bselzer.gw2.manager.common.dependency

import com.bselzer.gw2.manager.common.configuration.Configuration
import org.kodein.db.DB

interface RepositoryDependencies {
    val clients: Clients
    val configuration: Configuration
    val database: DB
    val preferences: Preferences
}