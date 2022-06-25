package com.bselzer.gw2.manager.common.repository.data.generic

import com.bselzer.gw2.v2.model.enumeration.WvwMapType
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveType

interface OwnerData {
    /**
     * The supported map types.
     */
    val mapTypes: List<WvwMapType>

    /**
     * The supported objective owners.
     */
    val owners: List<WvwObjectiveOwner>

    /**
     * The supported objective types.
     */
    val objectiveTypes: List<WvwObjectiveType>

    /**
     * The sum of the values in the map.
     */
    fun Map<out WvwObjectiveOwner?, Int>?.total(): Int
}