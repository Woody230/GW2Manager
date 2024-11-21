package com.bselzer.gw2.manager.common.database.query

import com.bselzer.gw2.manager.Guild as DbGuild
import com.bselzer.gw2.manager.GuildQueries
import com.bselzer.gw2.v2.model.guild.Guild as ApiGuild
import com.bselzer.gw2.v2.model.guild.GuildId

suspend fun GuildQueries.getById(
    id: GuildId,
    requestSingle: suspend () -> ApiGuild
): DbGuild {
    var dbModel = getById(id).executeAsOneOrNull()
    if (dbModel == null) {
        val apiModel = requestSingle()
        dbModel = DbGuild(
            Id = id,
            Name = apiModel.name
        )

        insertOrReplace(dbModel)
    }

    return dbModel
}