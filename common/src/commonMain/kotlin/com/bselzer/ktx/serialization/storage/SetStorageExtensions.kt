package com.bselzer.ktx.serialization.storage

import com.bselzer.ktx.value.identifier.Identifiable
import com.bselzer.ktx.value.identifier.Identifier

/**
 * Gets a model from the storage by its id.
 *
 * If the model does not exist, then the [requestModel] block is called and is written to the database if the [writeFilter] returns true.
 *
 * @param id the id to search for
 * @param requestModel the block for retrieving the model
 * @param writeFilter the block for determining whether to write the [requestModel] model to the database. Returning true writes the model.
 * @return the model with the [id]
 */
suspend fun <Id, Model> SetStorage<Id, Model>.getOrRequest(
    id: Id,
    requestModel: suspend (Id) -> Model,
    writeFilter: (Model) -> Boolean = { true }
): Model where Id: Any, Model: Any {
    var model = getOrNull(id)
    if (model == null) {
        model = requestModel(id)

        val shouldWrite = writeFilter(model)
        if (shouldWrite) {
            set(id, model)
        }
    }

    return model
}

/**
 * Finds missing models based on their id and puts them in storage.
 *
 * @param requestIds a block for retrieving all of the ids
 * @param requestModels a block for mapping ids to their associated models
 * @param getIdFromModel a block for getting the id of a model
 * @return the ids and models that already existed or retrieved by [requestModels]
 */
suspend fun <Id, Model> SetStorage<Id, Model>.getOrRequestMissing(
    requestIds: suspend () -> Collection<Id>,
    requestModels: suspend (Set<Id>) -> Collection<Model>,
    getIdFromModel: (Model) -> Id
): Map<Id, Model> where Id: Any, Model: Any {
    val allIds = requestIds().toHashSet()

    // TODO optimize by getting all models at once
    val models = allIds.associateWith { id -> getOrNull(id) }.toMutableMap()

    val missingIds = models.filter { entry -> entry.value == null }.keys
    if (missingIds.isNotEmpty()) {
        requestModels(missingIds).forEach { model ->
            val id =  getIdFromModel(model)
            models[id] = model
            set(id, model)
        }
    }

    // Ensure all non-existent models are purged before casting.
    return models.filterValues { value -> value != null } as Map<Id, Model>
}

/**
 * Finds missing models based on their id and puts them in storage.
 *
 * @param requestIds a block for retrieving all of the ids
 * @param requestModels a block for mapping ids to their associated models
 * @return the ids and models that already existed or retrieved by [requestModels]
 */
suspend fun <Id, IdValue, Model> SetStorage<Id, Model>.getOrRequestMissing(
    requestIds: suspend () -> Collection<Id>,
    requestModels: suspend (Set<Id>) -> Collection<Model>,
): Map<Id, Model> where Id: Identifier<IdValue>, Model : Identifiable<Id, IdValue> {
    return getOrRequestMissing(
        requestIds,
        requestModels,
        getIdFromModel = { model -> model.id }
    )
}

/**
 * Find models stored in storage.
 *
 * If there are no models in storage, then the [requestModels] block is called.
 *
 * @param requestModels the block for retrieving all of the models
 * @param getIdFromModel the block for retrieving the id from the model
 * @return all the models
 */
suspend fun <Id, Model> SetStorage<Id, Model>.getOrRequestOnce(
    requestModels: suspend () -> Collection<Model>,
    getIdFromModel: (Model) -> Id
): Collection<Model> where Id: Any, Model: Any {
    return getOrRequestByCount(minimum = 1, requestModels, getIdFromModel)
}

/**
 * Find models stored in storage.
 *
 * If there are no models in storage, then the [requestModels] block is called.
 *
 * @param requestModels the block for retrieving all of the models
 * @return all the models
 */
suspend fun <Id, IdValue, Model> SetStorage<Id, Model>.getOrRequestOnce(
    requestModels: suspend () -> Collection<Model>
): Collection<Model> where Id: Identifier<IdValue>, Model: Identifiable<Id, IdValue> {
    return getOrRequestByCount(minimum = 1, requestModels)
}

/**
 * Finds models stored in storage.
 *
 * If there are fewer than [minimum] number of models in storage, then the [requestModels] block is called.
 *
 * @param minimum the minimum number of database models required to not call the [requestModels] block
 * @param requestModels the block for retrieving all of the models
 * @param getIdFromModel the block for retrieving the id from the model
 * @return all the models
 */
suspend fun <Id, Model> SetStorage<Id, Model>.getOrRequestByCount(
    minimum: Int,
    requestModels: suspend () -> Collection<Model>,
    getIdFromModel: (Model) -> Id
): Collection<Model> where Id: Any, Model: Any {
    var stored = getAll()
    if (stored.count() < minimum) {
        // Clear existing entries and then request the current up-to-date models.
        removeAll()

        val requested = requestModels()
        requested.forEach { model ->
            val id = getIdFromModel(model)
            set(id, model)
        }

        stored = requested
    }

    return stored
}

/**
 * Finds models stored in storage.
 *
 * If there are fewer than [minimum] number of models in storage, then the [requestModels] block is called.
 *
 * @param minimum the minimum number of database models required to not call the [requestModels] block
 * @param requestModels the block for retrieving all of the models
 * @return all the models
 */
suspend fun <Id, IdValue, Model> SetStorage<Id, Model>.getOrRequestByCount(
    minimum: Int,
    requestModels: suspend () -> Collection<Model>
): Collection<Model> where Id: Identifier<IdValue>, Model: Identifiable<Id, IdValue> {
    return getOrRequestByCount(minimum, requestModels) { model -> model.id }
}
