package com.bselzer.gw2.manager.common.ui.layout.custom.objective.viewmodel

import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.objective.model.CoreData
import com.bselzer.gw2.v2.model.extension.wvw.level
import com.bselzer.gw2.v2.model.extension.wvw.tier
import com.bselzer.gw2.v2.model.extension.wvw.yakRatio
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.gw2.v2.resource.Gw2Resources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format

class CoreViewModel(
    context: AppComponentContext,
    matchObjective: WvwMapObjective,
    upgrade: WvwUpgrade?
) : ViewModel(context), CoreData {
    private val yaksDelivered = matchObjective.yaksDelivered
    override val pointsPerCapture: Pair<StringDesc, StringDesc> = matchObjective.pointsPerCapture()
    override val pointsPerTick: Pair<StringDesc, StringDesc> = matchObjective.pointsPerTick()
    override val yaks: Pair<StringDesc, StringDesc>? = upgrade?.yaks()
    override val progression: Pair<StringDesc, StringDesc>? = upgrade?.progression()

    private fun WvwMapObjective.pointsPerTick(): Pair<StringDesc, StringDesc> = Gw2Resources.strings.points_per_tick.desc() to pointsPerTick.toString().desc()
    private fun WvwMapObjective.pointsPerCapture(): Pair<StringDesc, StringDesc> = Gw2Resources.strings.points_per_capture.desc() to pointsPerCapture.toString().desc()

    private fun WvwUpgrade.yaks(): Pair<StringDesc, StringDesc> {
        val ratio = yakRatio(yaksDelivered)
        return AppResources.strings.yaks_delivered.desc() to AppResources.strings.yaks_delivered_ratio.format(ratio.first, ratio.second)
    }

    private fun WvwUpgrade.progression(): Pair<StringDesc, StringDesc> {
        val level = level(yaksDelivered)
        val tier = tier(yaksDelivered)?.name?.translated() ?: AppResources.strings.no_upgrade.desc()
        return AppResources.strings.upgrade_tier.desc() to AppResources.strings.upgrade_tier_level.format(tier, level, tiers.size)
    }
}