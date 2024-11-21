package com.bselzer.gw2.manager.common.database.adapter.wvw.upgrade

import app.cash.sqldelight.ColumnAdapter
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object WvwUpgradeColumnAdapter: ColumnAdapter<WvwUpgrade, String> {
    override fun decode(databaseValue: String): WvwUpgrade = Json.decodeFromString(databaseValue)
    override fun encode(value: WvwUpgrade): String = Json.encodeToString(value)
}