package com.bselzer.gw2.manager.common.ui.layout.splash.model.initialization.migration

import com.bselzer.gw2.manager.common.dependency.AppDependencies
import com.bselzer.gw2.v2.cache.operation.clearContinent
import com.bselzer.gw2.v2.cache.operation.clearGuild
import com.bselzer.gw2.v2.cache.operation.clearWvw
import com.bselzer.gw2.v2.tile.cache.operation.clearTile
import com.bselzer.ktx.compose.image.cache.operation.clearImage
import com.bselzer.ktx.kodein.db.transaction.transaction

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