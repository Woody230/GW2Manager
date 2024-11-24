package com.bselzer.ktx.serialization.storage

interface SetStorage<Id, Model> where Id: Any, Model: Any {
    suspend fun getAll(): Collection<Model>
    suspend fun getOrNull(id: Id): Model?
    suspend fun exists(id: Id): Boolean
    suspend fun set(id: Id, model: Model)
    suspend fun remove(id: Id)
    suspend fun removeAll()
}