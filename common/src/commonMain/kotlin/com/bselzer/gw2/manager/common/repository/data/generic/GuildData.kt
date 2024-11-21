package com.bselzer.gw2.manager.common.repository.data.generic

import com.bselzer.gw2.manager.Guild
import com.bselzer.gw2.manager.GuildUpgrade
import com.bselzer.gw2.v2.model.guild.GuildId
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgradeId

interface GuildData {
    val guilds: Map<GuildId, Guild>
    val guildUpgrades: Map<GuildUpgradeId, GuildUpgrade>
}