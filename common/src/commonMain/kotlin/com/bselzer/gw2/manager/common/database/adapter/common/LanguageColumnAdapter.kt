package com.bselzer.gw2.manager.common.database.adapter.common

import app.cash.sqldelight.ColumnAdapter
import com.bselzer.gw2.v2.client.model.Language

object LanguageColumnAdapter: ColumnAdapter<Language, String> {
    override fun decode(databaseValue: String): Language = Language(databaseValue)
    override fun encode(value: Language): String = value.value
}