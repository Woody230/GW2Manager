package com.bselzer.gw2.manager.common.repository.instance

import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.repository.base.AppRepository
import com.bselzer.gw2.manager.common.repository.model.continent.ContinentFloor
import com.bselzer.gw2.v2.model.continent.ContinentId
import com.bselzer.gw2.v2.model.continent.floor.FloorId
import com.bselzer.gw2.v2.model.map.MapId
import com.bselzer.ktx.kodein.db.operation.getById
import com.bselzer.ktx.kodein.db.transaction.Transaction
import com.bselzer.ktx.kodein.db.transaction.transaction

class ContinentRepository(
    dependencies: RepositoryDependencies
) : AppRepository(dependencies) {
    suspend fun getContinent(mapId: MapId): ContinentFloor = database.transaction().use {
        val map = getById(
            id = mapId,
            requestSingle = { clients.gw2.map.map(mapId) },
        )

        getContinentFloor(
            continentId = map.continentId,
            floorId = map.defaultFloorId
        )
    }

    /**
     * Gets the [ContinentFloor] associated with the configurable ids for the World vs. World continent.
     */
    suspend fun getWvwContinent() = database.transaction().use {
        getContinentFloor(
            continentId = ContinentId(configuration.wvw.map.continentId),
            floorId = FloorId(configuration.wvw.map.floorId)
        )
    }

    private suspend fun Transaction.getContinentFloor(
        continentId: ContinentId,
        floorId: FloorId
    ): ContinentFloor {
        val continent = getById(
            id = continentId,
            requestSingle = { clients.gw2.continent.continent(continentId) },
        )

        val floor = getById(
            id = floorId,
            requestSingle = { clients.gw2.continent.floor(continentId, floorId) }
        )

        return ContinentFloor(
            continent = continent,
            floor = floor
        )
    }
}