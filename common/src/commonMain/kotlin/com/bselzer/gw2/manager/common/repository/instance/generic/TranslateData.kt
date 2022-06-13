package com.bselzer.gw2.manager.common.repository.instance.generic

interface TranslateData {
    /**
     * @return the translated text associated with the given default text, or the default text if a translation does not exist
     */
    fun String.translated(): String
}