package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.ktx.aboutlibraries.distinct
import com.bselzer.ktx.aboutlibraries.sorted
import com.bselzer.ktx.resource.KtxResources
import com.mikepenz.aboutlibraries.entity.Library
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class LicenseViewModel(context: AppComponentContext) : MainViewModel(context) {
    override val title: StringDesc = KtxResources.strings.licenses.desc()
    override val libraries: List<Library> = super.libraries.sorted().distinct()
}