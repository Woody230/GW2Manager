package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map

import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.repository.data.specialized.SelectedWorldData
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.common.Image
import com.bselzer.gw2.manager.common.ui.layout.common.ImageImpl
import com.bselzer.gw2.v2.model.enumeration.WvwMapBonusType
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveType
import com.bselzer.gw2.v2.model.enumeration.extension.decodeOrNull
import com.bselzer.gw2.v2.model.tile.position.BoundedPosition
import com.bselzer.gw2.v2.model.tile.position.TexturePosition
import com.bselzer.gw2.v2.model.wvw.map.WvwMap
import com.bselzer.gw2.v2.resource.strings.stringDesc
import com.bselzer.ktx.logging.Logger
import dev.icerock.moko.resources.desc.image.asImageUrl
import dev.icerock.moko.resources.format

class BloodlustViewModel(
    context: AppComponentContext,
    borderland: WvwMap
) : ViewModel(context), SelectedWorldData by context.repositories.selectedWorld {
    val id: String = "bloodlust-${borderland.id}"

    private val matchRuins = borderland.objectives.filter { objective -> objective.type.decodeOrNull() == WvwObjectiveType.RUINS }
    private val objectiveRuins = matchRuins.mapNotNull { matchRuin -> objectives[matchRuin.id] }
    private val hasMatchRuins = matchRuins.isNotEmpty()
    private val hasObjectiveRuins = objectiveRuins.size == matchRuins.size
    val exists = hasMatchRuins && hasObjectiveRuins

    init {
        if (!hasMatchRuins) {
            Logger.w { "There are no ruins on map ${borderland.id}." }
        } else if (!hasObjectiveRuins) {
            Logger.w { "Mismatch between the number of ruins in the match and objectives on map ${borderland.id}." }
        }
    }

    // Use the center of all of the ruins as the position of the bloodlust icon.
    private val x = objectiveRuins.sumOf { ruin -> ruin.coordinates.x } / objectiveRuins.size
    private val y = objectiveRuins.sumOf { ruin -> ruin.coordinates.y } / objectiveRuins.size

    // Scale the coordinates to the zoom level and remove excluded bounds.
    val position: BoundedPosition = grid.bounded(TexturePosition(x, y))

    private val bonus = borderland.bonuses.firstOrNull { bonus -> bonus.type.decodeOrNull() == WvwMapBonusType.BLOODLUST }
    private val owner = bonus?.owner?.decodeOrNull() ?: WvwObjectiveOwner.NEUTRAL
    val image: Image = ImageImpl(
        image = configuration.wvw.bloodlust.iconLink.asImageUrl(),
        color = owner.color(),
        description = AppResources.strings.bloodlust_for.format(owner.stringDesc()),
    )
}