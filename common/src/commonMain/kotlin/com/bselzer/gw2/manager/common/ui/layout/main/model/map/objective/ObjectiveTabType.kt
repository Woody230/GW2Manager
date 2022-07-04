package com.bselzer.gw2.manager.common.ui.layout.main.model.map.objective

import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.ktx.resource.KtxResources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

enum class ObjectiveTabType {
    DETAILS,
    AUTOMATIC_UPGRADES,
    GUILD_IMPROVEMENTS,
    GUILD_TACTICS;

    fun stringDesc(): StringDesc = when (this) {
        DETAILS -> KtxResources.strings.details
        AUTOMATIC_UPGRADES -> Gw2Resources.strings.automatic_upgrades
        GUILD_IMPROVEMENTS -> Gw2Resources.strings.guild_improvements
        GUILD_TACTICS -> Gw2Resources.strings.guild_tactics
    }.desc()
}