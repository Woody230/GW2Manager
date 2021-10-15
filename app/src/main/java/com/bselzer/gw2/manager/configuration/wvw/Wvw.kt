package com.bselzer.gw2.manager.configuration.wvw

import com.bselzer.library.gw2.v2.model.enumeration.wvw.MapType
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import timber.log.Timber

@Serializable
@XmlSerialName(value = "WorldVsWorld", namespace = "", prefix = "")
data class Wvw(
    /**
     * The World vs. World maps.
     */
    val maps: List<WvwMap> = emptyList()
) {
    /**
     * @return the map with the given [type]
     */
    fun map(type: MapType): WvwMap {
        val map = maps.firstOrNull { map -> map.type == type }
        return if (map == null) {
            Timber.e("Missing WvW map type $type.")
            WvwMap(type = type)
        } else {
            map
        }
    }
}