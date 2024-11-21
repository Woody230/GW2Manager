package com.bselzer.gw2.manager.common.database.adapter.map

import app.cash.sqldelight.ColumnAdapter
import com.bselzer.gw2.v2.model.map.MapId

object MapIdColumnAdapter: ColumnAdapter<MapId, Long> {
    override fun decode(databaseValue: Long): MapId = MapId(databaseValue.toInt())
    override fun encode(value: MapId): Long = value.value.toLong()
}