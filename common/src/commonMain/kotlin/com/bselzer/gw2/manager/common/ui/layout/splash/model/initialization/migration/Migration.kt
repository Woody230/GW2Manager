package com.bselzer.gw2.manager.common.ui.layout.splash.model.initialization.migration

import com.bselzer.gw2.manager.common.dependency.AppDependencies

abstract class Migration(dependencies: AppDependencies) : AppDependencies by dependencies, Comparable<Migration> {
    abstract val from: Int
    abstract val to: Int
    abstract val reason: String

    abstract suspend fun migrate()

    override fun compareTo(other: Migration): Int = when {
        from > other.from -> 1
        from < other.from -> -1

        // Prioritize migrations that cover a greater span.
        to > other.to -> -1
        to < other.to -> 1
        else -> 0
    }
}