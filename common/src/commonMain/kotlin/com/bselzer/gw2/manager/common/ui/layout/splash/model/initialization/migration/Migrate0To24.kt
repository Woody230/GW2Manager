package com.bselzer.gw2.manager.common.ui.layout.splash.model.initialization.migration

import com.bselzer.gw2.manager.common.dependency.AppDependencies

class Migrate0To24(dependencies: AppDependencies) : Migration(dependencies) {
    override val from: Int = 0
    override val to: Int = 24
    override val reason: String = "New worlds created due to world restructuring."

    override suspend fun migrate() {
        preferences.wvw.selectedWorld.remove()
    }
}