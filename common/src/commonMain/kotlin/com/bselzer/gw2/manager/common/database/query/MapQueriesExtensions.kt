package com.bselzer.gw2.manager.common.database.query

import com.bselzer.gw2.manager.Map as DbMap
import com.bselzer.gw2.manager.MapQueries
import com.bselzer.gw2.v2.model.map.Map as ApiMap
import com.bselzer.gw2.v2.model.map.MapId

suspend fun MapQueries.getById(
    id: MapId,
    requestSingle: suspend () -> ApiMap
): ApiMap {
    val dbModel = getById(id).executeAsOneOrNull()
    var apiModel = dbModel?.Model
    if (apiModel == null) {
        apiModel = requestSingle()
        insertOrReplace(DbMap(
            Id = id,
            Model = apiModel
        ))
    }

    return apiModel
}

suspend fun MapQueries.putMissingById(
    requestIds: suspend () -> Collection<MapId>,
    requestById: suspend (Collection<MapId>) -> Collection<ApiMap>
): Map<MapId, ApiMap> {
    val allIds = requestIds().toHashSet()

    val apiModels = allIds
        .associateWith { id -> getById(id).executeAsOneOrNull()?.Model }
        .toMutableMap()

    val missingIds = apiModels.filter { entry -> entry.value == null }.keys
    if (missingIds.isNotEmpty()) {
        requestById(missingIds).forEach { apiModel ->
            apiModels[apiModel.id] = apiModel
            insertOrReplace(DbMap(
                Id = apiModel.id,
                Model = apiModel
            ))
        }
    }

    // Ensure all non-existent models are purged before casting.
    return apiModels.filterValues { value -> value != null } as Map<MapId, ApiMap>
}