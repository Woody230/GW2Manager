package com.bselzer.gw2.manager.common.repository.instance.generic

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.repository.data.generic.ColorData
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.extension.decodeOrNull
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective
import com.bselzer.ktx.compose.ui.graphics.color.Hex
import com.bselzer.ktx.compose.ui.graphics.color.color
import com.bselzer.ktx.compose.ui.graphics.color.hex
import com.bselzer.ktx.logging.Logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ColorRepository(
    dependencies: RepositoryDependencies
) : RepositoryDependencies by dependencies, ColorData {
    private val lock = Mutex()

    override val defaultColor: Color
        get() = colors[WvwObjectiveOwner.NEUTRAL] ?: Hex("#888888").color()

    private val _colors = mutableStateMapOf<WvwObjectiveOwner, Color>()
    val colors: Map<WvwObjectiveOwner, Color> = _colors

    override fun WvwMapObjective?.color(): Color {
        val owner = this?.owner?.decodeOrNull()
        return owner.color()
    }

    override fun WvwObjectiveOwner.hasDefaultColor(): Boolean = _colors[this] == preferences.wvw.colors.defaultValue[this]

    override fun WvwObjectiveOwner?.color(): Color = colors[this] ?: defaultColor

    override suspend fun setPreferenceColor(owner: WvwObjectiveOwner, color: Color) = lock.withLock {
        Logger.d { "Color | Updating $owner to $color equivalent to ${color.hex()}" }
        _colors[owner] = color

        val colors = preferences.wvw.colors.get().toMutableMap()
        colors[owner] = color
        preferences.wvw.colors.set(colors)
    }

    override suspend fun setPreferenceColors() = lock.withLock {
        Logger.d { "Color | Updating with preference colors." }

        // In case there is a mixing of defaults and preferences set, need to merge them together.
        val colors = preferences.wvw.colors.defaultValue + preferences.wvw.colors.get()
        colors.forEach { (owner, color) ->
            _colors[owner] = color
        }
    }

    override suspend fun resetPreferenceColor(owner: WvwObjectiveOwner): Unit = lock.withLock {
        Logger.d { "Color | Resetting $owner." }

        val colors = preferences.wvw.colors.get().toMutableMap()
        colors.remove(owner)
        preferences.wvw.colors.set(colors)

        preferences.wvw.colors.defaultValue[owner]?.let { defaultColor ->
            _colors[owner] = defaultColor
        }
    }
}