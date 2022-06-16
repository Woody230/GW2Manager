package com.bselzer.gw2.manager.common.repository.instance.generic

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.v2.model.enumeration.WvwMapType
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective

interface ColorData {
    /**
     * The supported map types.
     */
    val mapTypes: List<WvwMapType>

    /**
     * The supported objective owners.
     */
    val owners: List<WvwObjectiveOwner>

    val defaultColor: Color

    /**
     * Gets the [Color] for the current owner of the objective.
     */
    fun WvwMapObjective?.color(): Color
    fun WvwObjectiveOwner?.color(): Color

    /**
     * @return true if the owner is using the default (configured) color instead of a user defined (preference) color
     */
    fun WvwObjectiveOwner.hasConfiguredColor(): Boolean

    suspend fun setPreferenceColor(owner: WvwObjectiveOwner, color: Color)
    suspend fun setPreferenceColors()
    suspend fun resetPreferenceColor(owner: WvwObjectiveOwner)
}