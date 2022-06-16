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
    fun getColor(objective: WvwMapObjective?): Color

    fun getColor(owner: WvwObjectiveOwner?): Color

    suspend fun setPreferenceColor(owner: WvwObjectiveOwner, color: Color)
    suspend fun setPreferenceColors()
    suspend fun resetPreferenceColor(owner: WvwObjectiveOwner)
}