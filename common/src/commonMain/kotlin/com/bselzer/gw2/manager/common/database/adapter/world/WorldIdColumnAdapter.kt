package com.bselzer.gw2.manager.common.database.adapter.world

import app.cash.sqldelight.ColumnAdapter
import com.bselzer.gw2.v2.model.world.WorldId

object WorldIdColumnAdapter: ColumnAdapter<WorldId, Long> {
    override fun decode(databaseValue: Long): WorldId = WorldId(databaseValue.toInt())
    override fun encode(value: WorldId): Long = value.value.toLong()
}