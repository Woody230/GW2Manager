package com.bselzer.gw2.manager.common.database.adapter.world

import app.cash.sqldelight.ColumnAdapter
import com.bselzer.gw2.v2.model.enumeration.wrapper.WorldName

object WorldNameColumnAdapter: ColumnAdapter<WorldName, String> {
    override fun decode(databaseValue: String): WorldName = WorldName(databaseValue)
    override fun encode(value: WorldName): String = value.value
}