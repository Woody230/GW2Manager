package com.bselzer.gw2.manager.common.database.adapter.wvw.match

import app.cash.sqldelight.ColumnAdapter
import com.bselzer.gw2.v2.model.wvw.match.WvwMatchId

object WvwMatchIdColumnAdapter: ColumnAdapter<WvwMatchId, String> {
    override fun decode(databaseValue: String): WvwMatchId = WvwMatchId(databaseValue)
    override fun encode(value: WvwMatchId): String = value.value
}