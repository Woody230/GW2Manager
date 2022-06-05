package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.main.model.about.NoticeResources
import com.bselzer.gw2.manager.common.ui.layout.main.model.about.VersionCode
import com.bselzer.gw2.manager.common.ui.layout.main.model.about.VersionName
import com.bselzer.gw2.manager.common.ui.layout.main.model.about.VersionResources
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.ktx.resource.KtxResources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class AboutViewModel(context: AppComponentContext) : MainViewModel(context) {
    override val title: StringDesc = AppResources.strings.app_name.desc()

    val versionResources
        get() = VersionResources(
            code = VersionCode(
                title = KtxResources.strings.version_code.desc(),
                subtitle = build.VERSION_CODE.toString().desc()
            ),
            name = VersionName(
                title = KtxResources.strings.version_name.desc(),
                subtitle = build.VERSION_NAME.desc()
            )
        )

    val noticeResources
        get() = NoticeResources(
            title = KtxResources.strings.legal_notice.desc(),
            subtitle = Gw2Resources.strings.ncsoft_notice.desc()
        )
}