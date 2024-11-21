package com.bselzer.gw2.manager.common.database.adapter.continent

import app.cash.sqldelight.ColumnAdapter
import com.bselzer.gw2.v2.model.continent.Continent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object ContinentColumnAdapter: ColumnAdapter<Continent, String> {
    override fun decode(databaseValue: String): Continent = Json.decodeFromString(databaseValue)
    override fun encode(value: Continent): String = Json.encodeToString(value)
}