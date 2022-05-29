package com.bselzer.gw2.manager.common.repository.instance.generic

import androidx.compose.runtime.mutableStateMapOf
import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.repository.instance.AppRepository
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.world.WorldId
import com.bselzer.ktx.kodein.db.operation.findAllOnce
import com.bselzer.ktx.kodein.db.transaction.transaction

class WorldRepository(
    dependencies: RepositoryDependencies
) : AppRepository(dependencies) {
    private val _worlds = mutableStateMapOf<WorldId, World>()
    val worlds: Map<WorldId, World> = _worlds

    suspend fun updateWorlds() = database.transaction().use {
        findAllOnce { clients.gw2.world.worlds() }.forEach { world -> _worlds[world.id] = world }
    }
}