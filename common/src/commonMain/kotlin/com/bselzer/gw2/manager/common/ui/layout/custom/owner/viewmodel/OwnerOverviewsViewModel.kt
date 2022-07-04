package com.bselzer.gw2.manager.common.ui.layout.custom.owner.viewmodel

import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.dependency.ViewModelDependencies
import com.bselzer.gw2.manager.common.repository.data.specialized.SelectedWorldData
import com.bselzer.gw2.manager.common.ui.layout.custom.owner.model.*
import com.bselzer.gw2.v2.model.enumeration.WvwMapBonusType
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.extension.decodeOrNull
import com.bselzer.gw2.v2.model.extension.wvw.count.ObjectiveOwnerCount
import com.bselzer.gw2.v2.model.extension.wvw.count.WvwMatchObjectiveOwnerCount
import com.bselzer.gw2.v2.model.extension.wvw.count.WvwSkirmishObjectiveOwnerCount
import com.bselzer.gw2.v2.model.extension.wvw.linkedWorlds
import com.bselzer.gw2.v2.model.extension.wvw.owner
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.gw2.v2.resource.strings.stringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.asImageDesc
import dev.icerock.moko.resources.desc.image.asImageUrl
import dev.icerock.moko.resources.format

interface OwnerOverviewsViewModel : ViewModelDependencies, SelectedWorldData {
    /**
     * The data and indicator overview for each of the [owners].
     */
    val overviews: List<OwnerOverview>
        get() = owners.map { owner ->
            OwnerOverview(
                victoryPoints = count.victoryPoints(owner),
                pointsPerTick = count.pointsPerTick(owner),
                skirmishWarScore = lastSkirmish.skirmishWarScore(owner),
                totalWarScore = count.totalWarScore(owner),
                owner = owner.owner(),
                home = match.home(owner),
                bloodlusts = match.bloodlusts(owner)
            )
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
        icon = Gw2Resources.images.war_score.asImageDesc(),
        description = Gw2Resources.strings.points_per_tick.desc(),
    )

    private fun WvwSkirmishObjectiveOwnerCount.skirmishWarScore(owner: WvwObjectiveOwner): Data = Data(
        data = scores.getCoerced(owner).toString().desc(),
        icon = configuration.wvw.icons.warScore.asImageUrl(),
        description = Gw2Resources.strings.skirmish_score.desc(),
        color = owner.color()
    )

    private fun ObjectiveOwnerCount.totalWarScore(owner: WvwObjectiveOwner): Data = Data(
        data = scores.getCoerced(owner).toString().desc(),
        icon = configuration.wvw.icons.warScore.asImageUrl(),
        description = Gw2Resources.strings.total_score.desc(),
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