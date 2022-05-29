package com.bselzer.gw2.manager.common.dependency

import com.bselzer.gw2.manager.BuildKonfig
import com.bselzer.gw2.manager.common.configuration.Configuration
import com.bselzer.gw2.manager.common.repository.instance.Repositories
import com.mikepenz.aboutlibraries.entity.Library
import org.kodein.db.DB

interface Dependencies {
    val isDebug: Boolean
    val database: DB
    val build: BuildKonfig
    val clients: Clients
    val preferences: Preferences
    val configuration: Configuration
    val repositories: Repositories
    val libraries: List<Library>
}