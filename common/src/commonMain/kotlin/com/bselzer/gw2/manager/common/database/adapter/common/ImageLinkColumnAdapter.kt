package com.bselzer.gw2.manager.common.database.adapter.common

import app.cash.sqldelight.ColumnAdapter
import com.bselzer.gw2.v2.model.wrapper.ImageLink

object ImageLinkColumnAdapter: ColumnAdapter<ImageLink, String> {
    override fun decode(databaseValue: String): ImageLink = ImageLink(databaseValue)
    override fun encode(value: ImageLink): String = value.value
}