package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.repository.data.specialized.SelectedWorldData
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.chart.viewmodel.ChartDataViewModel
import com.bselzer.gw2.manager.common.ui.layout.chart.viewmodel.ChartViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.SelectedWorldRefreshAction.Companion.refreshAction
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.overview.*
import com.bselzer.gw2.v2.model.enumeration.WvwMapBonusType
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.extension.decodeOrNull
import com.bselzer.gw2.v2.model.extension.wvw.count.ObjectiveOwnerCount
import com.bselzer.gw2.v2.model.extension.wvw.count.WvwMatchObjectiveOwnerCount
import com.bselzer.gw2.v2.model.extension.wvw.linkedWorlds
import com.bselzer.gw2.v2.model.extension.wvw.objectiveOwnerCount
import com.bselzer.gw2.v2.model.extension.wvw.owner
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.world.WorldId
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.gw2.v2.resource.strings.stringDesc
import com.bselzer.ktx.resource.KtxResources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.asImageDesc
import dev.icerock.moko.resources.desc.image.asImageUrl
import dev.icerock.moko.resources.format

class WvwMatchOverviewViewModel(
    context: AppComponentContext
) : MainViewModel(context), SelectedWorldData by context.repositories.selectedWorld {
    override val title: StringDesc = KtxResources.strings.overview.desc()

    override val actions
        get() = listOf(refreshAction())

    /**
     * Creates the state for the module displaying the user's choice of world.
     */
    val selectedWorld: WorldResources
        get() {
            val selectedId = world?.id ?: WorldId(0)
            val owner = match?.owner(selectedId) ?: WvwObjectiveOwner.NEUTRAL
            return WorldResources(
                title = Gw2Resources.strings.world.desc(),
                subtitle = worldSubtitle(selectedId, world),
                color = owner.color(),
                image = Gw2Resources.images.rank_dolyak,
            )
        }

    /**
     * Creates the subtitle of the world using the name of the world if it exists.
     */
    private fun worldSubtitle(id: WorldId, world: World?): StringDesc = when {
        // If we can locate the world, then display the choice.
        world != null -> world.name.value.translated().desc()

        // If the user has not selected a world, then use the default message a preference displays.
        id.isDefault -> KtxResources.strings.not_set.desc()

        // We know that a world has been selected but it currently doesn't exist for some reason.
        else -> KtxResources.strings.unknown.desc()
    }

    /**
     * The overview for the selected world's match.
     */
    val chart: ChartViewModel?
        get() = match?.objectiveOwnerCount()?.let { count ->
            ChartViewModel(
                context = this,
                data = count.victoryPoints,
            )
        }

    /**
     * The description of the [chart] overview for the selected world's match.
     */
    val chartDescription: ChartDataViewModel?
        get() = match?.objectiveOwnerCount()?.let { count ->
            ChartDataViewModel(
                context = this,
                data = count.victoryPoints,
                title = Gw2Resources.strings.victory_points.desc(),
            )
        }

    /**
     * The data and indicator overview for each of the [owners].
     */
    val overviews: List<OwnerOverview>
        get() {
            val match = match ?: return emptyList()
            val count = match.objectiveOwnerCount()
            return owners.map { owner ->
                OwnerOverview(
                    victoryPoints = count.victoryPoints(owner),
                    pointsPerTick = count.pointsPerTick(owner),
                    warScore = count.warScore(owner),
                    owner = owner.owner(),
                    home = match.home(owner),
                    bloodlusts = match.bloodlusts(owner)
                )
            }
        }

    private fun WvwObjectiveOwner.owner(): Owner = Owner(
        name = displayableLinkedWorlds(this),
        color = color()
    )

    private fun WvwMatchObjectiveOwnerCount.victoryPoints(owner: WvwObjectiveOwner): Data = Data(
        data = victoryPoints.getCoerced(owner).toString().desc(),
        icon = Gw2Resources.images.victory_points.asImageDesc(),
        description = Gw2Resources.strings.victory_points.desc(),
    )

    private fun ObjectiveOwnerCount.pointsPerTick(owner: WvwObjectiveOwner): Data = Data(
        data = pointsPerTick.getCoerced(owner).toString().desc(),
        icon = configuration.wvw.icons.pointsPerTick.asImageUrl(),
        description = Gw2Resources.strings.points_per_tick.desc(),
    )

    private fun ObjectiveOwnerCount.warScore(owner: WvwObjectiveOwner): Data = Data(
        data = scores.getCoerced(owner).toString().desc(),
        icon = configuration.wvw.icons.warScore.asImageUrl(),
        description = Gw2Resources.strings.war_score.desc(),
        color = owner.color()
    )

    private fun Map<WvwObjectiveOwner, Int>.getCoerced(owner: WvwObjectiveOwner) = this[owner]?.coerceAtLeast(0) ?: 0

    /**
     * Creates the [Home] if the user's selected world matches one of the [owner]'s worlds.
     */
    private fun WvwMatch.home(owner: WvwObjectiveOwner): Home? = when (
        linkedWorlds(owner).contains(worldId)
    ) {
        false -> null
        true -> Home(
            icon = configuration.wvw.icons.home.asImageUrl(),
            color = owner.color(),
            description = AppResources.strings.home_world.desc()
        )
    }

    /**
     * Creates the bloodlust icons associated with the maps that the given [owner] controls.
     */
    private fun WvwMatch.bloodlusts(owner: WvwObjectiveOwner): List<Bloodlust> = bloodlustedMaps(owner).map { map ->
        val mapOwner = map.type.decodeOrNull()?.owner() ?: WvwObjectiveOwner.NEUTRAL
        Bloodlust(
            icon = configuration.wvw.icons.bloodlust.asImageUrl(),
            color = mapOwner.color(),
            description = AppResources.strings.bloodlust_for.format(mapOwner.stringDesc())
        )
    }

    /**
     * Finds the maps where the bloodlust bonus is for the given [owner].
     */
    private fun WvwMatch.bloodlustedMaps(owner: WvwObjectiveOwner) = maps.filter { map ->
        val bonus = map.bonuses.firstOrNull { bonus -> bonus.type.decodeOrNull() == WvwMapBonusType.BLOODLUST }
        bonus?.owner?.decodeOrNull() == owner
    }
}