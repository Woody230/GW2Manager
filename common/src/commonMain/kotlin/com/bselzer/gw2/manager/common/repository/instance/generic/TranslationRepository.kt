package com.bselzer.gw2.manager.common.repository.instance.generic

import androidx.compose.runtime.mutableStateMapOf
import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.dependency.Singleton
import com.bselzer.gw2.manager.common.repository.data.generic.TranslateData
import com.bselzer.gw2.v2.intl.cache.operation.putMissingTranslations
import com.bselzer.gw2.v2.intl.translation.Translator
import com.bselzer.ktx.function.collection.putInto
import com.bselzer.ktx.intl.DefaultLocale
import com.bselzer.ktx.intl.Locale
import com.bselzer.ktx.intl.Localizer
import com.bselzer.ktx.kodein.db.transaction.transaction
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.value.identifier.Identifiable
import com.bselzer.ktx.value.identifier.Identifier
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import com.bselzer.gw2.v2.client.model.Language as WrapperLanguage
import com.bselzer.gw2.v2.model.enumeration.Language as EnumLanguage

@Singleton
@Inject
class TranslationRepository(
    dependencies: RepositoryDependencies,
) : RepositoryDependencies by dependencies, TranslateData {
    override val languages = listOf(Localizer.ENGLISH, Localizer.FRENCH, Localizer.GERMAN, Localizer.SPANISH)

    private val listeners: MutableList<(Locale) -> Unit> = mutableListOf()
    private val _translations = mutableStateMapOf<String, String>()
    val translations: Map<String, String> = _translations

    /**
     * @return the translated text associated with the given default text, or the default text if a translation does not exist
     */
    override fun String.translated(): String = translations[this] ?: this

    /**
     * Adds a listener to be notified of locale changes.
     */
    fun addListener(listener: (Locale) -> Unit) {
        listeners.add(listener)
    }

    override fun updateLocale(locale: Locale) {
        scope.launch {
            Logger.i { "Translation | Locale | Updating to '$locale'." }
            preferences.common.locale.set(locale)
            setLocale(locale)
        }
    }

    override fun resetLocale() {
        scope.launch {
            val default = preferences.common.locale.defaultValue
            Logger.i { "Translation | Locale | Resetting to '$default'." }

            preferences.common.locale.remove()
            setLocale(default)
        }
    }

    private fun setLocale(locale: Locale) {
        // Default locale is used by the date time formatters.
        DefaultLocale = locale

        // Localizer locale is used as a composition local for translations.
        Localizer.locale = locale

        // Remove the existing locale's translations.
        _translations.clear()

        // Notify listeners about the change so that translations can be updated.
        listeners.forEach { listener -> listener(locale) }
    }

    /**
     * Updates the translations for the [Model]s if the current locale is not [Localizer.ENGLISH].
     */
    suspend fun <Model : Identifiable<Id, Value>, Id : Identifier<Value>, Value> updateTranslations(
        translator: Translator<Model>,
        defaults: Collection<Model>,
        requestTranslated: suspend (Collection<Id>, WrapperLanguage) -> Collection<Model>
    ) {
        val language = Localizer.locale.language()

        // Don't need a translation for the default.
        if (language == EnumLanguage.ENGLISH) {
            Logger.d { "Translation | Skipping update since language is English." }
            return
        }

        Logger.d { "Translation | Updating for ${defaults.size} models." }
        database.transaction().use {
            putMissingTranslations(
                translator = translator,
                defaults = defaults,
                language = WrapperLanguage(Json.encodeToString(language)),
                requestTranslated = requestTranslated
            ).putInto(_translations)
        }
    }

    /**
     * Converts the [Locale] to its associated Guild Wars 2 supported language.
     */
    private fun Locale.language() = when (this) {
        Localizer.FRENCH -> EnumLanguage.FRENCH
        Localizer.GERMAN -> EnumLanguage.GERMAN
        Localizer.SPANISH -> EnumLanguage.SPANISH
        else -> EnumLanguage.ENGLISH
    }
}