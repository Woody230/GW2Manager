package com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.viewmodel

import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model.Upgrade
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgrade
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.ImageDesc
import dev.icerock.moko.resources.desc.image.asImageUrl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GuildUpgradeViewModel(
    context: AppComponentContext,
    upgrade: GuildUpgrade,
    isUnlocked: Boolean
) : ViewModel(context), Upgrade {
    override val name: StringDesc = upgrade.name.translated().desc()
    override val link: ImageDesc = upgrade.iconLink.value.asImageUrl()
    override val description: StringDesc = upgrade.description.translated().desc()
    override val alpha: Flow<Float> = flow {
        val alpha = configuration.alpha(condition = isUnlocked)
        emit(alpha)
    }
}