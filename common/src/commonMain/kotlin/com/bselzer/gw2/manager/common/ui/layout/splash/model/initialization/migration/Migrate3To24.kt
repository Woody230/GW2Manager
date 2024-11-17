package com.bselzer.gw2.manager.common.ui.layout.splash.model.initialization.migration

import com.bselzer.gw2.manager.common.dependency.AppDependencies
import com.bselzer.gw2.v2.intl.model.Translation
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.ktx.db.operation.clear
import com.bselzer.ktx.db.transaction.transaction

class Migrate3To24(dependencies: AppDependencies) : Migration(dependencies) {
    override val from: Int = 3
    override val to: Int = 24
    override val reason: String = "New worlds created due to world restructuring. The language for translations was being stored with quotes."

    override suspend fun migrate(): Unit = database.transaction().use {
        clear<World>()
        clear<Translation>()

        preferences.wvw.selectedWorld.remove()
    }
}