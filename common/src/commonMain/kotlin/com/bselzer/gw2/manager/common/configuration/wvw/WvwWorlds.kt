package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.gw2.v2.client.model.Language
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.ktx.serialization.context.JsonContext
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwWorlds {
    @XmlSerialName(value = "hardcoded", namespace = "", prefix = "")
    val hardcoded: Boolean = false

    @XmlSerialName(value = "World", namespace = "", prefix = "")
    val worlds: List<WvwWorld> = emptyList()

    val models: List<World>
        get() = worlds.map { hardcodedWorld ->
            World(
                id = hardcodedWorld.id,
                name = hardcodedWorld.englishName
            )
        }

    fun translatedModels(language: Language): List<World> {
        val languageEnum = with (JsonContext) {
            language.value.decodeOrNull<com.bselzer.gw2.v2.model.enumeration.Language>()
        }

        if (languageEnum == null || languageEnum == com.bselzer.gw2.v2.model.enumeration.Language.ENGLISH) {
            return emptyList()
        }

        return worlds.map { hardcodedWorld ->
            World(
                id = hardcodedWorld.id,
                name = when (languageEnum) {
                    com.bselzer.gw2.v2.model.enumeration.Language.SPANISH -> hardcodedWorld.spanishName
                    com.bselzer.gw2.v2.model.enumeration.Language.GERMAN -> hardcodedWorld.germanName
                    com.bselzer.gw2.v2.model.enumeration.Language.FRENCH -> hardcodedWorld.frenchName
                    else -> hardcodedWorld.englishName
                }
            )
        }
    }
}