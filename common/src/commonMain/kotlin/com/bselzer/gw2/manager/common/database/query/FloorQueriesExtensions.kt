package com.bselzer.gw2.manager.common.database.query

import com.bselzer.gw2.manager.Floor as DbFloor
import com.bselzer.gw2.manager.FloorQueries
import com.bselzer.gw2.v2.model.continent.floor.Floor as ApiFloor
import com.bselzer.gw2.v2.model.continent.floor.FloorId

suspend fun FloorQueries.getById(
    id: FloorId,
    requestSingle: suspend () -> ApiFloor
): ApiFloor {
    val dbModel = getById(id).executeAsOneOrNull()
    var apiModel = dbModel?.Model
    if (apiModel == null) {
        apiModel = requestSingle()
        insertOrReplace(
            DbFloor(
                Id = id,
                Model = apiModel
            )
        )
    }

    return apiModel
}