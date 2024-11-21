package com.bselzer.gw2.manager.common.database.adapter.map

import app.cash.sqldelight.ColumnAdapter
import com.bselzer.gw2.v2.model.map.Map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object MapColumnAdapter: ColumnAdapter<Map, String> {
    override fun decode(databaseValue: String): Map = Json.decodeFromString(databaseValue)
    override fun encode(value: Map): String = Json.encodeToString(value)
}