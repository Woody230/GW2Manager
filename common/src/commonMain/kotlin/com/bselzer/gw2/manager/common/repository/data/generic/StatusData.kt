package com.bselzer.gw2.manager.common.repository.data.generic

import androidx.compose.runtime.State
import com.bselzer.ktx.resource.KtxResources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

interface StatusData {
    val status: State<Gw2ApiStatus>

    /**
     * Determines what the status of the API is and updates the [status].
     */
    suspend fun updateStatus()

    /**
     * If [isSuccess], then update the [status] to available.
     * Otherwise if the [status] is currently available, then determine what the true status of the API is.
     */
    suspend fun questionStatus(isSuccess: Boolean)
}

enum class Gw2ApiStatusType {
    Available,
    Unavailable
}

data class Gw2ApiStatus(
    val type: Gw2ApiStatusType,
    val message: String
) {
    companion object {
        val AVAILABLE = Gw2ApiStatus(type = Gw2ApiStatusType.Available, message = "")
    }

    override fun toString(): String = buildString {
        append(type)

        if (message.isNotEmpty()) {
            append(" - $message")
        }
    }

    fun desc(): StringDesc = when (type) {
        Gw2ApiStatusType.Available -> KtxResources.strings.available.desc()
        Gw2ApiStatusType.Unavailable -> KtxResources.strings.unavailable.desc()
    }
}