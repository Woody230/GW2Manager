package com.bselzer.gw2.manager.common.database.adapter.floor

import app.cash.sqldelight.ColumnAdapter
import com.bselzer.gw2.v2.model.continent.floor.Floor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object FloorColumnAdapter: ColumnAdapter<Floor, String> {
    override fun decode(databaseValue: String): Floor = Json.decodeFromString(databaseValue)
    override fun encode(value: Floor): String = Json.encodeToString(value)
}