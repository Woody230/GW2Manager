package com.bselzer.gw2.manager.common.ui.layout.splash.model.initialization.migration

import com.bselzer.gw2.manager.common.dependency.AppDependencies
import com.bselzer.ktx.logging.Logger
import korlibs.io.file.deleteRecursively
import korlibs.io.file.std.localVfs
import kotlinx.io.files.Path

class Migrate24To25(dependencies: AppDependencies) : Migration(dependencies) {
    override val from: Int = 24
    override val to: Int = 25
    override val reason: String = "Removal of Kodein-DB library."

    override suspend fun migrate() {
        val kodeinDbPath = Path(legacyDatabaseDirectory, "Gw2Database")
        val kodeinDbDirectory = localVfs(kodeinDbPath.toString())
        if (kodeinDbDirectory.exists()) {
            Logger.d { "Migration | $from to $to | Kodein DB directory exists." }
            kodeinDbDirectory.deleteRecursively(true)
        }
        else {
            Logger.d { "Migration | $from to $to | Kodein DB directory does not exist." }
        }
    }
}