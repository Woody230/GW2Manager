package com.bselzer.gw2.manager.common.database.adapter.floor

import app.cash.sqldelight.ColumnAdapter
import com.bselzer.gw2.v2.model.continent.floor.FloorId

object FloorIdColumnAdapter: ColumnAdapter<FloorId, Long> {
    override fun decode(databaseValue: Long): FloorId = FloorId(databaseValue.toInt())
    override fun encode(value: FloorId): Long = value.value.toLong()
}