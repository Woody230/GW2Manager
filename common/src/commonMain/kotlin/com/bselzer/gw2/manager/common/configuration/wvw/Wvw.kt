package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.v2.model.enumeration.extension.enumValueOrNull
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.ktx.datetime.format.FormatStyle
import com.bselzer.ktx.datetime.format.FormatStyleDateTimeFormatter
import com.bselzer.ktx.datetime.format.PatternDateTimeFormatter
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.format
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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
    val chart: WvwChart = WvwChart(),

    @XmlSerialName(value = "ContestedAreas", namespace = "", prefix = "")
    val contestedAreas: WvwContestedAreas = WvwContestedAreas()
) {
    /**
     * @return the objective from the configuration associated with the endpoint objective
     */
    fun objective(objective: WvwObjective?) = objective?.let {
        objectives.objectives.firstOrNull { it.type == objective.type.enumValueOrNull() }
    }

    @Transient
    private val timeFormatter = FormatStyleDateTimeFormatter(dateStyle = null, timeStyle = FormatStyle.SHORT)

    @Transient
    private val dayOfWeekFormatter = PatternDateTimeFormatter("EEEE")

    fun claimedAt(instant: Instant): StringDesc = AppResources.strings.claimed_at.format(
        timeFormatter.format(instant),
        dayOfWeekFormatter.format(instant)
    )

    fun flippedAt(instant: Instant): StringDesc = AppResources.strings.flipped_at.format(
        timeFormatter.format(instant),
        dayOfWeekFormatter.format(instant)
    )
}