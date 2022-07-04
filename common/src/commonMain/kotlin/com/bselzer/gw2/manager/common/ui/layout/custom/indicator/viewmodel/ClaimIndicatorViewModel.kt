package com.bselzer.gw2.manager.common.ui.layout.custom.indicator.viewmodel

import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.common.ImageAdapter
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.ImageDesc
import dev.icerock.moko.resources.desc.image.asImageUrl

class ClaimIndicatorViewModel(
    context: AppComponentContext,
    matchObjective: WvwMapObjective
) : ViewModel(context), ImageAdapter {
    override val enabled: Boolean = !matchObjective.claimedBy?.value.isNullOrBlank()
    override val image: ImageDesc? = configuration.wvw.objectives.claim.iconLink?.asImageUrl()
    override val description: StringDesc = AppResources.strings.claimed.desc()
}