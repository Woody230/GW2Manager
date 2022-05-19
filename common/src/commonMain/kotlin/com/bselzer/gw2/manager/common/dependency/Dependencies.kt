package com.bselzer.gw2.manager.common.dependency

import com.bselzer.gw2.manager.common.configuration.Configuration
import com.mikepenz.aboutlibraries.entity.Library

interface Dependencies : KodeinTransaction {
    override val caches: Caches
    val clients: Clients
    val preferences: Preferences
    val configuration: Configuration
    val repositories: Repositories
    val libraries: List<Library>
}