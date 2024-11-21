package com.bselzer.gw2.manager.common.database.adapter.continent

import app.cash.sqldelight.ColumnAdapter
import com.bselzer.gw2.v2.model.continent.ContinentId

object ContinentIdColumnAdapter: ColumnAdapter<ContinentId, Long> {
    override fun decode(databaseValue: Long): ContinentId = ContinentId(databaseValue.toInt())
    override fun encode(value: ContinentId): Long = value.value.toLong()
}