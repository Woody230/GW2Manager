package com.bselzer.gw2.manager.common.storage

import kotlinx.serialization.Serializable

@Serializable
data class TranslationId(
    val default: String,
    val language: String
)