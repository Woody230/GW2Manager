package com.bselzer.gw2.manager.common.repository.instance.specialized

import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgrade
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgradeId
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.model.wvw.objective.WvwMapObjectiveId
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgradeId
import dev.icerock.moko.resources.desc.StringDesc

interface MatchData {
    val match: WvwMatch?
    val objectives: Map<WvwMapObjectiveId, WvwObjective>
    val upgrades: Map<WvwUpgradeId, WvwUpgrade>
    val guildUpgrades: Map<GuildUpgradeId, GuildUpgrade>

    /**
     * @return the displayable names for the linked worlds associated with the objective [owner]
     */
    fun displayableLinkedWorlds(owner: WvwObjectiveOwner): StringDesc
}