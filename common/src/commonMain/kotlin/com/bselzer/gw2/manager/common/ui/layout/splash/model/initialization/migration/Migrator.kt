package com.bselzer.gw2.manager.common.ui.layout.splash.model.initialization.migration

import com.bselzer.gw2.manager.common.dependency.AppDependencies
import com.bselzer.ktx.logging.Logger

class Migrator(
    dependencies: AppDependencies
) {
    private val migrations: Collection<Migration> = listOf(
        Migrate0To3(dependencies)
    ).sorted()

    /**
     * Migrates from the given version to the latest version.
     * @return the version migrated to
     */
    suspend fun migrate(from: Int): Int {
        Logger.i { "Migrating from $from." }

        var intermediate = from
        migrations.forEach { migration ->
            if (intermediate in migration.from until migration.to) {
                Logger.i { "Migration | ${migration.from} to ${migration.to}" }
                migration.migrate()
                intermediate = migration.to
            }
        }

        Logger.i { "Migrated to $intermediate." }
        return intermediate
    }
}