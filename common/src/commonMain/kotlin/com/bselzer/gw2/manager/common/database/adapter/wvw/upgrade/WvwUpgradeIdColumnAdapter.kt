package com.bselzer.gw2.manager.common.database.adapter.wvw.upgrade

import app.cash.sqldelight.ColumnAdapter
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgradeId

object WvwUpgradeIdColumnAdapter: ColumnAdapter<WvwUpgradeId, Long> {
    override fun decode(databaseValue: Long): WvwUpgradeId = WvwUpgradeId(databaseValue.toInt())
    override fun encode(value: WvwUpgradeId): Long = value.value.toLong()
}