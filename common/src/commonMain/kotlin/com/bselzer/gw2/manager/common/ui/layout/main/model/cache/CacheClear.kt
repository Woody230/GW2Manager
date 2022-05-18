package com.bselzer.gw2.manager.common.ui.layout.main.model.cache

import com.bselzer.ktx.kodein.db.transaction.Transaction
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc

data class CacheClear(
    val image: ImageResource,
    val title: StringDesc,
    val subtitle: StringDesc,
    val perform: Transaction.() -> Unit
)