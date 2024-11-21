package com.bselzer.gw2.manager.common.database.adapter.wvw.match

import app.cash.sqldelight.ColumnAdapter
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object WvwMatchColumnAdapter: ColumnAdapter<WvwMatch, String> {
    override fun decode(databaseValue: String): WvwMatch = Json.decodeFromString(databaseValue)
    override fun encode(value: WvwMatch): String = Json.encodeToString(value)
}