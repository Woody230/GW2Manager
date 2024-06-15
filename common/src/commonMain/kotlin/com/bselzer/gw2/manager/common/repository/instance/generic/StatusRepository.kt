package com.bselzer.gw2.manager.common.repository.instance.generic

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.repository.data.generic.Gw2ApiStatus
import com.bselzer.gw2.manager.common.repository.data.generic.Gw2ApiStatusType
import com.bselzer.gw2.manager.common.repository.data.generic.StatusData
import com.bselzer.ktx.logging.Logger
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

class StatusRepository(
    dependencies: RepositoryDependencies,
) : RepositoryDependencies by dependencies, StatusData {
    private companion object {
        const val URL = "https://api.guildwars2.com/v2.json?v=latest"
    }

    private val _status = mutableStateOf(Gw2ApiStatus.AVAILABLE)
    override val status: State<Gw2ApiStatus> = _status

    private fun setStatus(status: Gw2ApiStatus) {
        Logger.i { "Status | $status" }
        _status.value = status
    }


    override suspend fun updateStatus() {
        setStatus(fetchStatus())
    }

    override suspend fun questionStatus(isSuccess: Boolean) {
        if (isSuccess) {
            setStatus(Gw2ApiStatus.AVAILABLE)
            return
        }

        // We currently think the API is available but the result contradicts that expectation.
        // Consequently, resolve the true state of the API.
        if (status.value == Gw2ApiStatus.AVAILABLE) {
            updateStatus()
        }
    }

    // TODO move to GW2 wrapper
    private suspend fun fetchStatus(): Gw2ApiStatus = try {
        val response = clients.http.get(URL)
        when (response.status.isSuccess()) {
            true -> Gw2ApiStatus.AVAILABLE
            else -> Gw2ApiStatus(
                type = Gw2ApiStatusType.Unavailable,
                message = response.bodyAsText()
            )
        }
    } catch (ex: Exception) {
        Logger.e(ex, "Failed to request $URL in order to determine the status of the GW2 API.")
        Gw2ApiStatus(Gw2ApiStatusType.Unavailable, "Unable to fetch the status of the GW2 API.")
    }
}