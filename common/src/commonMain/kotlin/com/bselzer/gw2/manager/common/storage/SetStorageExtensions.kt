package com.bselzer.gw2.manager.common.storage

import com.bselzer.gw2.v2.client.model.Language
import com.bselzer.gw2.v2.intl.model.Translation
import com.bselzer.gw2.v2.intl.translation.Translator
import com.bselzer.ktx.serialization.storage.SetStorage
import com.bselzer.ktx.value.identifier.Identifiable
import com.bselzer.ktx.value.identifier.Identifier

/**
 * Finds missing translations from the [defaults] models and puts them in the database.
 *
 * @param translator the translator for retrieving translations
 * @param defaults the models with default text
 * @param language the language to request translations for
 * @param requestTranslated a block for retrieving a map of the default model to the translated model
 * @return the default text mapped to the translated text
 */
suspend fun <Id, IdValue, Model> SetStorage<TranslationId, Translation>.getOrRequestMissing(
    translator: Translator<Model>,
    defaults: Collection<Model>,
    language: Language,
    requestTranslated: suspend (Set<Id>, Language) -> Collection<Model>
): Map<String, String> where Id: Identifier<IdValue>, Model: Identifiable<Id, IdValue> {
    val defaultToTranslation = mutableMapOf<String, String>()

    // Find all the translations currently being stored.
    val texts = defaults.associateWith { default -> translator.texts(default) }
    val translations: Map<Model, List<Translation>> = texts.mapValues { model ->
        model.value.mapNotNull { default ->
            val id = TranslationId(default, language.value)
            getOrNull(id)?.also { translation ->
                defaultToTranslation[default] = translation.translated
            }
        }
    }

    // If we are missing one of the translations, then we will need to make a request for the translations associated with the model.
    val missing = texts.filter { model ->
        val translation = translations[model.key] ?: emptyList()
        translation.size < model.value.size
    }

    if (missing.isNotEmpty()) {
        val missingIds = missing.keys.map { default -> default.id }.toHashSet()
        val translated = requestTranslated(missingIds, language).associateBy { translated -> translated.id }

        missing.keys.associateWith { default -> translated[default.id] }.forEach { (default, translated) ->
            if (translated != null) {
                translator.translations(default = default, translated = translated, language = language.value).forEach { translation ->
                    defaultToTranslation[translation.default] = translation.translated

                    val id = translation.toTranslationId()
                    set(id, translation)
                }
            }
        }
    }

    return defaultToTranslation
}