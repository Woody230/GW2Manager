package com.bselzer.gw2.manager.common.repository.instance.generic

import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.dependency.Singleton
import com.bselzer.gw2.manager.common.repository.data.generic.OwnerData
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import me.tatarka.inject.annotations.Inject

@Singleton
@Inject
class OwnerRepository(
    dependencies: RepositoryDependencies
) : RepositoryDependencies by dependencies, OwnerData {
    override val mapTypes = configuration.wvw.supported.mapTypes
    override val owners = configuration.wvw.supported.owners
    override val objectiveTypes = configuration.wvw.supported.objectiveTypes

    /**
     * Gets the sum of all the values in the map whose owner exists in the collection of supported [owners].
     */
    override fun Map<out WvwObjectiveOwner?, Int>?.total(): Int = when {
        this == null -> 0
        else -> filterKeys { owner -> owners.contains(owner) }.values.sum()
    }
}