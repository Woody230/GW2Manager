package com.bselzer.gw2.manager.common.repository.instance.generic

import androidx.compose.runtime.mutableStateMapOf
import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.repository.instance.AppRepository
import com.bselzer.gw2.v2.intl.translation.Gw2Translators
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.world.WorldId
import com.bselzer.ktx.kodein.db.operation.findAllOnce
import com.bselzer.ktx.kodein.db.transaction.transaction
import com.bselzer.ktx.logging.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorldRepository(
    dependencies: RepositoryDependencies,
    private val translation: TranslationRepository
) : AppRepository(dependencies) {
    init {
        translation.addListener {
            CoroutineScope(Dispatchers.Default).launch {
                updateWorlds()
            }
        }
    }

    private val _worlds = mutableStateMapOf<WorldId, World>()
    val worlds: Map<WorldId, World> = _worlds

    suspend fun updateWorlds() = database.transaction().use {
        Logger.d { "World | Updating all worlds." }

        val worlds = findAllOnce { clients.gw2.world.worlds() }.onEach { world -> _worlds[world.id] = world }
        translation.updateTranslations(
            translator = Gw2Translators.world,
            defaults = worlds,
            requestTranslated = { missing, language -> clients.gw2.world.worlds(missing, language) }
        )
    }
}