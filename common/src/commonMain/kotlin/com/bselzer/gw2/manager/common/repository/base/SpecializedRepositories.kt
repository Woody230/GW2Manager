package com.bselzer.gw2.manager.common.repository.base

import com.bselzer.gw2.manager.common.repository.instance.SelectedWorldRepository

/**
 * Repositories that provide specialized data from general repositories.
 */
interface SpecializedRepositories {
    val selectedWorld: SelectedWorldRepository
}