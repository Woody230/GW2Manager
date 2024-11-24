package com.bselzer.ktx.serialization.storage

fun interface IdEncoder<Id, Value> {
    fun encode(id: Id): Value
}