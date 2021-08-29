package com.bselzer.library.kotlin.extension.function.collection

/**
 * Adds [this] to the collection.
 */
fun <T> T.addTo(collection: MutableCollection<T>) = collection.add(this)