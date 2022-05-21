package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import com.bselzer.gw2.manager.common.Gw2Resources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.main.model.about.NoticeResources
import com.bselzer.gw2.manager.common.ui.layout.main.model.about.VersionCode
import com.bselzer.gw2.manager.common.ui.layout.main.model.about.VersionName
import com.bselzer.gw2.manager.common.ui.layout.main.model.about.VersionResources
import com.bselzer.ktx.resource.Resources
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class AboutViewModel(context: AppComponentContext) : MainViewModel(context) {
    override val title: StringDesc = Gw2Resources.strings.app_name.desc()

    val versionResources
        get() = VersionResources(
            code = VersionCode(
                title = Resources.strings.version_code.desc(),
                subtitle = StringDesc.Raw(configuration.app.versionCode.toString())
            ),
            name = VersionName(
                title = Resources.strings.version_name.desc(),
                subtitle = StringDesc.Raw(configuration.app.versionName)
            )
        )

    val noticeResources
        get() = NoticeResources(
            title = Resources.strings.legal_notice.desc(),
            subtitle = Gw2Resources.strings.ncsoft_notice.desc()
        )
}