package com.bselzer.gw2.manager.common.ui.layout.splash.model.initialization.migration

import com.bselzer.gw2.manager.common.dependency.Dependencies
import com.bselzer.ktx.logging.Logger

class Migrator(
    dependencies: Dependencies
) {
    private val migrations: Collection<Migration> = listOf(
        Migrate0To3(dependencies)
    ).sorted()

    /**
     * Migrates from the given version to the latest version.
     * @return the version migrated to
     */
    suspend fun migrate(from: Int): Int {
        Logger.d { "Migrating from $from." }

        var intermediate = from
        migrations.forEach { migration ->
            if (intermediate in migration.from until migration.to) {
                Logger.d { "Migration | ${migration.from} to ${migration.to}" }
                migration.migrate()
                intermediate = migration.to
            }
        }

        Logger.d { "Migrated to $intermediate." }
        return intermediate
    }
}