package com.bselzer.ktx.serialization.storage

import io.github.irgaly.kottage.Kottage
import io.github.irgaly.kottage.KottageList
import io.github.irgaly.kottage.KottageStorage
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class KottageSetStorage<Id, Model> @PublishedApi internal constructor(
    private val kottage: KottageList,
    private val type: KType
): SetStorage<Id, Model> where Id: Any, Model: Any {
    override suspend fun getAll(): Collection<Model> {
        var entry = kottage.getFirst()

        val models = mutableListOf<Model>()
        while (entry != null) {
            val model = entry.value<Model>(type)
            models.add(model)

            val nextId = entry.nextPositionId
            entry = if (nextId == null) null else kottage.get(nextId)
        }

        return models
    }

    override suspend fun getOrNull(id: Id): Model? {
        return kottage.get(id.positionId())?.value(type)
    }

    override suspend fun exists(id: Id): Boolean {
        return kottage.get(id.positionId()) != null
    }

    override suspend fun set(id: Id, model: Model) {
        if (exists(id)) {
            remove(id)
        }

        kottage.add(id.positionId(), model, type)
    }

    override suspend fun remove(id: Id) {
        kottage.remove(id.positionId(), removeItemFromStorage = true)
    }

    override suspend fun removeAll() {
        kottage.removeAll(removeItemFromStorage = true)
    }

    private fun Id.positionId() = toString()
}

inline fun <Id, reified Model> kottageSetStorage(
    kottage: KottageList
): KottageSetStorage<Id, Model> where Id: Any, Model: Any {
    return KottageSetStorage(
        kottage,
        typeOf<Model>()
    )
}

inline fun <Id, reified Model> kottageSetStorage(
    kottage: KottageStorage,
    name: String
): KottageSetStorage<Id, Model> where Id: Any, Model: Any {
    val list = kottage.list("$name.List")
    return kottageSetStorage<Id, Model>(list)
}

inline fun <Id, reified Model> kottageSetStorage(
    kottage: Kottage,
    name: String
) : KottageSetStorage<Id, Model> where Id: Any, Model: Any {
    val storage = kottage.storage("$name.Storage")
    return kottageSetStorage<Id, Model>(storage, name)
}