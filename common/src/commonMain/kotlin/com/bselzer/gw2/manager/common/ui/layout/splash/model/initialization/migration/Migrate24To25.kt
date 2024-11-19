package com.bselzer.gw2.manager.common.ui.layout.splash.model.initialization.migration

import com.bselzer.gw2.manager.common.dependency.AppDependencies
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.ktx.db.operation.clear
import com.bselzer.ktx.db.transaction.transaction

class Migrate24To25(dependencies: AppDependencies) : Migration(dependencies) {
    override val from: Int = 24
    override val to: Int = 25
    override val reason: String = "Dolyak requirements updated (https://en-forum.guildwars2.com/topic/153597-game-update-notes-november-19-2024/)"

    override suspend fun migrate(): Unit = database.transaction().use {
        clear<WvwUpgrade>()
    }
}