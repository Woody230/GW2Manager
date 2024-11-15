package com.bselzer.gw2.manager.common.repository.instance.generic

import androidx.compose.runtime.mutableStateMapOf
import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.v2.intl.translation.Gw2Translators
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.world.WorldId
import com.bselzer.ktx.db.operation.findAllOnce
import com.bselzer.ktx.db.transaction.transaction
import com.bselzer.ktx.logging.Logger

class WorldRepository(
    dependencies: RepositoryDependencies,
    private val repositories: Repositories
) : RepositoryDependencies by dependencies {
    data class Repositories(
        val translation: TranslationRepository
    )

    private val _worlds = mutableStateMapOf<WorldId, World>()
    val worlds: Map<WorldId, World> = _worlds

    suspend fun updateWorlds() = database.transaction().use {
        Logger.d { "World | Updating all worlds." }

        // TODO migrate to using new worlds from api
        val worlds = when (configuration.wvw.worlds.hardcoded) {
            false -> findAllOnce { clients.gw2.world.worlds() }
            true -> configuration.wvw.worlds.worlds.map { hardcodedWorld ->
                World(
                    id = hardcodedWorld.id,
                    name = hardcodedWorld.englishName
                )
            }
        }

        for (world in worlds) {
            Logger.d { "World | Id = ${world.id}, Name = ${world.name}"}
            _worlds[world.id] = world
        }

        repositories.translation.updateTranslations(
            translator = Gw2Translators.world,
            defaults = worlds,
            requestTranslated = { missing, language ->
                // TODO migrate to using new worlds from api
                when (configuration.wvw.worlds.hardcoded) {
                    false -> clients.gw2.world.worlds(missing, language)
                    true -> configuration.wvw.worlds.translatedModels(language)
                }
            }
        )
    }
}