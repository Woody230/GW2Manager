package com.bselzer.gw2.manager.common.ui.layout.custom.claim.viewmodel

import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.v2.emblem.request.EmblemRequest
import com.bselzer.gw2.v2.emblem.request.EmblemRequestOptions
import com.bselzer.gw2.v2.model.guild.Guild
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc
import dev.icerock.moko.resources.desc.image.asImageUrl
import dev.icerock.moko.resources.format

class ClaimImageViewModel(
    context: AppComponentContext,
    guild: Guild
) : ViewModel(context) {
    private companion object {
        const val SIZE = 256
    }

    val request: EmblemRequest = run {
        val options = arrayOf(EmblemRequestOptions.MAXIMIZE_BACKGROUND_ALPHA)
        clients.emblem.requestEmblem(guild.id.value, size = SIZE, *options)
    }

    val link: ImageDesc = clients.emblem.emblemUrl(request).asImageUrl()
    val description: StringDesc = AppResources.strings.guild_emblem.format(guild.name.translated())
    val size: Int = SIZE
}