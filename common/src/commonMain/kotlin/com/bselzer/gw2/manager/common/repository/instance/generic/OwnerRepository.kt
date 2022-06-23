package com.bselzer.gw2.manager.common.repository.instance.generic

import com.bselzer.gw2.manager.common.dependency.Singleton
import com.bselzer.gw2.manager.common.repository.data.generic.OwnerData
import com.bselzer.gw2.v2.model.enumeration.WvwMapType
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import me.tatarka.inject.annotations.Inject

@Singleton
@Inject
class OwnerRepository : OwnerData {
    override val mapTypes = listOf(
        WvwMapType.ETERNAL_BATTLEGROUNDS,
        WvwMapType.BLUE_BORDERLANDS,
        WvwMapType.GREEN_BORDERLANDS,
        WvwMapType.RED_BORDERLANDS
    )

    override val owners: List<WvwObjectiveOwner> = listOf(
        WvwObjectiveOwner.BLUE,
        WvwObjectiveOwner.GREEN,
        WvwObjectiveOwner.RED
    )

    /**
     * Gets the sum of all the values in the map whose owner exists in the collection of supported [owners].
     */
    override fun Map<out WvwObjectiveOwner?, Int>?.total(): Int = when {
        this == null -> 0
        else -> filterKeys { owner -> owners.contains(owner) }.values.sum()
    }
}