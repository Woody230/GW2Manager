package com.bselzer.gw2.manager.common.ui.layout.splash.model.initialization.migration

import com.bselzer.gw2.manager.common.dependency.AppDependencies
import com.bselzer.gw2.v2.db.operation.clearContinent
import com.bselzer.gw2.v2.db.operation.clearGuild
import com.bselzer.gw2.v2.db.operation.clearTile
import com.bselzer.gw2.v2.db.operation.clearWvw
import com.bselzer.ktx.db.operation.clearImage
import com.bselzer.ktx.db.transaction.transaction

class Migrate0To3(dependencies: AppDependencies) : Migration(dependencies) {
    override val from: Int = Int.MIN_VALUE
    override val to: Int = 3
    override val reason: String = "Many model changes, including the use of value classes for ids."

    override suspend fun migrate() = database.transaction().use {
        clearContinent()
        clearTile()
        clearGuild()
        clearWvw()
        clearImage()
    }
}