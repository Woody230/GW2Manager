package com.bselzer.gw2.manager.common.ui.layout.custom.indicator.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.common.Image
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.ImageDesc
import dev.icerock.moko.resources.desc.image.asImageUrl

class ObjectiveImageViewModel(
    context: AppComponentContext,
    objective: WvwObjective,
    matchObjective: WvwMapObjective,
) : ViewModel(context), Image {
    private val configObjective = configuration.wvw.objective(objective)

    override val alpha: Float = DefaultAlpha
    override val enabled: Boolean = true

    override val description: StringDesc = objective.name.translated().desc()
    override val color: Color = matchObjective.color()

    // Use a default link when the icon link doesn't exist. The link won't exist for atypical types such as Spawn/Mercenary.
    override val image: ImageDesc = objective.iconLink.value.ifBlank { configObjective?.defaultIconLink ?: "" }.asImageUrl()
}