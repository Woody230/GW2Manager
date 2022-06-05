package com.bselzer.gw2.manager.common.configuration.wvw

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.extension.enumValueOrNull
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.ktx.compose.ui.graphics.color.Hex
import com.bselzer.ktx.compose.ui.graphics.color.color
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class Wvw(
    @XmlSerialName(value = "Objectives", namespace = "", prefix = "")
    val objectives: WvwObjectives = WvwObjectives(),

    @XmlSerialName(value = "Map", namespace = "", prefix = "")
    val map: WvwMap = WvwMap(),

    @XmlSerialName(value = "Bloodlust", namespace = "", prefix = "")
    val bloodlust: WvwBloodlust = WvwBloodlust(),

    @XmlSerialName(value = "Chart", namespace = "", prefix = "")
    val chart: WvwChart = WvwChart()
) {
    /**
     * @return the objective from the configuration associated with the endpoint objective
     */
    fun objective(objective: WvwObjective?) = objective?.let {
        objectives.objectives.firstOrNull { it.type == objective.type.enumValueOrNull() }
    }

    /**
     * @return the color associated with the endpoint objective
     */
    fun color(objective: WvwMapObjective?): Color = color(objective?.owner?.enumValueOrNull())

    /**
     * @return the color associated with an objective [owner]
     */
    fun color(owner: WvwObjectiveOwner?, default: String = "#888888") = Hex(objectives.hex(owner = owner ?: WvwObjectiveOwner.NEUTRAL, default = default)).color()

    /**
     * @return the date/time instant to a displayable formatted string
     */
    fun selectedDateFormatted(instant: Instant): String = objectives.selected.dateFormatter.format(instant)
}