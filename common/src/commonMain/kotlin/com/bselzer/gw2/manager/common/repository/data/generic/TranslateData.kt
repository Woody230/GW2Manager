package com.bselzer.gw2.manager.common.repository.data.generic

import com.bselzer.ktx.intl.Locale

interface TranslateData {
    /**
     * The supported languages.
     */
    val languages: List<Locale>

    /**
     * @return the translated text associated with the given default text, or the default text if a translation does not exist
     */
    fun String.translated(): String

    fun updateLocale(locale: Locale)
    fun resetLocale()
}