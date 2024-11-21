package com.bselzer.gw2.manager.common.database.query

import com.bselzer.gw2.manager.GuildUpgrade as DbGuildUpgrade
import com.bselzer.gw2.manager.GuildUpgradeQueries
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgrade as ApiGuildUpgrade
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgradeId

suspend fun GuildUpgradeQueries.putMissingById(
    requestIds: suspend () -> Collection<GuildUpgradeId>,
    requestById: suspend (Collection<GuildUpgradeId>) -> Collection<ApiGuildUpgrade>
): Map<GuildUpgradeId, DbGuildUpgrade> {
    val allIds = requestIds().toHashSet()

    val dbModels = allIds
        .associateWith { id -> getById(id).executeAsOneOrNull() }
        .toMutableMap()

    val missingIds = dbModels.filter { entry -> entry.value == null }.keys
    if (missingIds.isNotEmpty()) {
        requestById(missingIds).forEach { apiModel ->
            val dbModel = DbGuildUpgrade(
                Id = apiModel.id,
                Name = apiModel.name,
                Description = apiModel.description,
                IconLink = apiModel.iconLink
            )
            dbModels[apiModel.id] = dbModel
            insertOrReplace(dbModel)
        }
    }

    // Ensure all non-existent models are purged before casting.
    return dbModels.filterValues { value -> value != null } as Map<GuildUpgradeId, DbGuildUpgrade>
}