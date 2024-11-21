package com.bselzer.gw2.manager.common.database.adapter.guild

import app.cash.sqldelight.ColumnAdapter
import com.bselzer.gw2.v2.model.guild.GuildId

object GuildIdColumnAdapter: ColumnAdapter<GuildId, String> {
    override fun decode(databaseValue: String): GuildId = GuildId(databaseValue)
    override fun encode(value: GuildId): String = value.value
}