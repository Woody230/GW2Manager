package com.bselzer.gw2.manager.common.ui.layout.custom.chart.viewmodel

import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.repository.data.specialized.SelectedWorldData
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.common.ImageImpl
import com.bselzer.gw2.manager.common.ui.layout.common.image
import com.bselzer.gw2.manager.common.ui.layout.custom.chart.model.Chart
import com.bselzer.gw2.manager.common.ui.layout.custom.chart.model.ChartSlice
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.resource.strings.stringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.asImageUrl
import dev.icerock.moko.resources.format

class ChartViewModel(
    context: AppComponentContext,
    private val data: Map<out WvwObjectiveOwner?, Int>?,
) : ViewModel(context), SelectedWorldData by context.repositories.selectedWorld {
    val chart: Chart
        get() = Chart(
            background = configuration.wvw.chart.backgroundLink.image(),
            divider = configuration.wvw.chart.dividerLink.image(),
            slices = slices
        )

    /**
     * Creates a neutral slice and a slice for each of the [owners] using the proportioned amount defined by the [data].
     * The neutral slice is added first to act as the background behind the owned slices.
     */
    private val slices: Collection<ChartSlice>
        get() = listOf(neutralSlice) + ownerSlices

    private val neutralSlice: ChartSlice
        get() = ChartSlice(
            startAngle = 0f,
            endAngle = 0f,
            image = ImageImpl(
                description = AppResources.strings.neutral_slice.desc(),
                image = configuration.wvw.chart.neutralLink.asImageUrl(),
                color = WvwObjectiveOwner.NEUTRAL.color()
            )
        )

    private val ownerSlices: Collection<ChartSlice>
        get() {
            val total = data.total().toFloat()
            var startAngle = 0f
            return owners.map { owner ->
                val amount = data?.get(owner) ?: 0
                val angle = if (total <= 0) 360f / owners.size else amount / total * 360f

                val hasDefaultColor = owner.hasDefaultColor()
                ChartSlice(
                    startAngle = startAngle,
                    endAngle = startAngle + angle,
                    image = ImageImpl(
                        description = AppResources.strings.owned_slice.format(angle, owner.stringDesc()),
                        color = if (hasDefaultColor) null else owner.color(),
                        // If using the default color, then use the same color slice as it is in game.
                        image = if (hasDefaultColor) {
                            owner.link().asImageUrl()
                        } else {
                            // Otherwise, change the tint using the blank neutral slice.
                            configuration.wvw.chart.neutralLink.asImageUrl()
                        },
                    )
                ).also {
                    // Set up the next slice.
                    startAngle += angle
                }
            }
        }

    private fun WvwObjectiveOwner.link() = when (this) {
        WvwObjectiveOwner.RED -> configuration.wvw.chart.redLink
        WvwObjectiveOwner.BLUE -> configuration.wvw.chart.blueLink
        WvwObjectiveOwner.GREEN -> configuration.wvw.chart.greenLink
        WvwObjectiveOwner.NEUTRAL -> configuration.wvw.chart.neutralLink
    }
}