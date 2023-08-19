package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.ColorViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.LanguageViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.MapLabelViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.RefreshIntervalViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.StatusViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.ThemeViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.TokenViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.ZoomViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.model.settings.WvwResources
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.ktx.datetime.format.DurationBound
import com.bselzer.ktx.resource.KtxResources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class SettingsViewModel(context: AppComponentContext) : MainViewModel(context) {
    override val title: StringDesc = KtxResources.strings.settings.desc()

    // TODO split settings into separate view models (common and wvw)

    val theme: ThemeViewModel
        get() = ThemeViewModel(context = this)

    val token: TokenViewModel
        get() = TokenViewModel(context = this)

    val wvwResources: WvwResources
        get() = WvwResources(
            image = Gw2Resources.images.rank_dolyak,
            title = Gw2Resources.strings.wvw.desc(),
        )

    val refreshInterval: RefreshIntervalViewModel
        get() = RefreshIntervalViewModel(
            context = this,
            setting = preferences.wvw.refreshInterval,
            intervalBound = DurationBound(min = 30.seconds),
            units = listOf(DurationUnit.SECONDS, DurationUnit.MINUTES, DurationUnit.HOURS, DurationUnit.DAYS)
        )

    val language: LanguageViewModel
        get() = LanguageViewModel(context = this)

    val zoom: ZoomViewModel
        get() = ZoomViewModel(context = this)

    val mapLabel: MapLabelViewModel
        get() = MapLabelViewModel(context = this)

    val colors: List<ColorViewModel>
        get() = mapTypes.map { mapType ->
            ColorViewModel(
                context = this,
                mapType = mapType
            )
        }

    val status: StatusViewModel
        get() = StatusViewModel(context = this)
}