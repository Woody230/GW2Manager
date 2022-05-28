package com.bselzer.gw2.manager.common.dependency

import com.bselzer.gw2.manager.common.configuration.Configuration
import kotlinx.coroutines.CoroutineScope
import org.kodein.db.DB

interface RepositoryDependencies {
    val database: DB
    val preferences: Preferences
    val scope: CoroutineScope
    val clients: Clients
    val configuration: Configuration
}