package com.bselzer.gw2.manager.common.database.adapter.wvw.objective

import app.cash.sqldelight.ColumnAdapter
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjectiveId

object WvwObjectiveIdColumnAdapter: ColumnAdapter<WvwObjectiveId, Long>{
    override fun decode(databaseValue: Long): WvwObjectiveId = WvwObjectiveId(databaseValue.toInt())
    override fun encode(value: WvwObjectiveId): Long = value.value.toLong()
}