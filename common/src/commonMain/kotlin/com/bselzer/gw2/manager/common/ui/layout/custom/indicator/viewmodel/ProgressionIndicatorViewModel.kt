package com.bselzer.gw2.manager.common.ui.layout.custom.indicator.viewmodel

import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.configuration.wvw.WvwUpgradeProgression
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.common.ImageAdapter
import com.bselzer.gw2.v2.model.extension.wvw.level
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc
import dev.icerock.moko.resources.desc.image.asImageUrl
import dev.icerock.moko.resources.format

class ProgressionIndicatorViewModel(
    context: AppComponentContext,
    matchObjective: WvwMapObjective,
    upgrade: WvwUpgrade?
) : ViewModel(context), ImageAdapter {
    // Get the progression level associated with the current number of yaks delivered to the objective.
    private val level: Int? = upgrade?.level(matchObjective.yaksDelivered)
    private val progression: WvwUpgradeProgression? = level?.let { configuration.wvw.objectives.progressions.getOrNull(level) }

    override val enabled: Boolean = progression != null
    override val image: ImageDesc? = progression?.indicatorLink?.asImageUrl()
    override val description: StringDesc? = level?.let { AppResources.strings.upgrade_level.format(level) }
}