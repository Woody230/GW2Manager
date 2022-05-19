package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.ktx.resource.Resources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class AboutViewModel(context: AppComponentContext) : MainViewModel(context) {
    override val title: StringDesc = Resources.strings.about.desc()
}