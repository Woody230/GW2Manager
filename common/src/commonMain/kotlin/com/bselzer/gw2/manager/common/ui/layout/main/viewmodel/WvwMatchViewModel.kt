package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import com.bselzer.gw2.manager.common.Gw2Resources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class WvwMatchViewModel(context: AppComponentContext) : MainViewModel(context) {
    override val title: StringDesc = Gw2Resources.strings.wvw_match.desc()
}