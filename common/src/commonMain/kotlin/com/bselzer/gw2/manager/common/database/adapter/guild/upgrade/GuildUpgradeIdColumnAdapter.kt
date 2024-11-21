package com.bselzer.gw2.manager.common.database.adapter.guild.upgrade

import app.cash.sqldelight.ColumnAdapter
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgradeId

object GuildUpgradeIdColumnAdapter: ColumnAdapter<GuildUpgradeId, Long> {
    override fun decode(databaseValue: Long): GuildUpgradeId = GuildUpgradeId(databaseValue.toInt())
    override fun encode(value: GuildUpgradeId): Long = value.value.toLong()
}