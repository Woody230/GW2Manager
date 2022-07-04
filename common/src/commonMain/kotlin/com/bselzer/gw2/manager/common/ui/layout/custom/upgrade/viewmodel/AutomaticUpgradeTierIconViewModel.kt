package com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.configuration.wvw.WvwUpgradeProgression
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model.UpgradeTierIcon
import com.bselzer.gw2.v2.model.wvw.upgrade.tier.WvwUpgradeTier
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc
import dev.icerock.moko.resources.desc.image.asImageUrl
import dev.icerock.moko.resources.format
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class AutomaticUpgradeTierIconViewModel(
    context: AppComponentContext,
    progression: WvwUpgradeProgression,
    tier: WvwUpgradeTier,
    yakRatio: Pair<Int, Int>,
    isUnlocked: Boolean,
) : ViewModel(context), UpgradeTierIcon {
    private val tierName: String = tier.name.translated()

    private val initialAlpha = configuration.alpha(condition = isUnlocked)
    private val initialDescription = AppResources.strings.upgrade_tier_yaks.format(tierName, yakRatio.first, yakRatio.second)

    override val alpha: Flow<Float> = flowOf(initialAlpha)
    override val description: Flow<StringDesc> = flowOf(initialDescription)
    override val link: ImageDesc = progression.iconLink.asImageUrl()

    override val color: Color?
        @Composable
        get() = null
}