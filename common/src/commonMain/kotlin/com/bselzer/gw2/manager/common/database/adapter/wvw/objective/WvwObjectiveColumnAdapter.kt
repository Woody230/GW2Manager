package com.bselzer.gw2.manager.common.database.adapter.wvw.objective

import app.cash.sqldelight.ColumnAdapter
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object WvwObjectiveColumnAdapter: ColumnAdapter<WvwObjective, String> {
    override fun decode(databaseValue: String): WvwObjective = Json.decodeFromString(databaseValue)
    override fun encode(value: WvwObjective): String = Json.encodeToString(value)
}