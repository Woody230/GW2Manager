package com.bselzer.gw2.manager.common.repository.instance

import com.bselzer.gw2.manager.common.repository.instance.generic.GenericRepositories
import com.bselzer.gw2.manager.common.repository.instance.specialized.SelectedWorldRepository
import com.bselzer.gw2.manager.common.repository.instance.specialized.SpecializedRepositories

interface Repositories : GenericRepositories, SpecializedRepositories {
    val selectedWorld: SelectedWorldRepository
}