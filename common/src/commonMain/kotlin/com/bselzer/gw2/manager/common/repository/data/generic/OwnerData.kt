package com.bselzer.gw2.manager.common.repository.data.generic

import com.bselzer.gw2.v2.model.enumeration.WvwMapType
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner

interface OwnerData {
    /**
     * The supported map types.
     */
    val mapTypes: List<WvwMapType>

    /**
     * The supported objective owners.
     */
    val owners: List<WvwObjectiveOwner>

    fun Map<out WvwObjectiveOwner?, Int>?.total(): Int
}