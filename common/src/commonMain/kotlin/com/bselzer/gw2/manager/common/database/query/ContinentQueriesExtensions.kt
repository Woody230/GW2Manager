package com.bselzer.gw2.manager.common.database.query

import com.bselzer.gw2.manager.Continent as DbContinent
import com.bselzer.gw2.manager.ContinentQueries
import com.bselzer.gw2.v2.model.continent.Continent as ApiContinent
import com.bselzer.gw2.v2.model.continent.ContinentId

suspend fun ContinentQueries.getById(
    id: ContinentId,
    requestSingle: suspend () -> ApiContinent
): ApiContinent {
    val dbModel = getById(id).executeAsOneOrNull()
    var apiModel = dbModel?.Model
    if (apiModel == null) {
        apiModel = requestSingle()
        insertOrReplace(
            DbContinent(
                Id = id,
                Model = apiModel
            )
        )
    }

    return apiModel
}