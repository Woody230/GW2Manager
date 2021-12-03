package com.bselzer.gw2.manager.android.ui.activity.wvw

import com.bselzer.gw2.manager.android.ui.coil.HexColorTransformation
import com.bselzer.gw2.manager.common.configuration.wvw.Wvw
import com.bselzer.library.gw2.v2.model.enumeration.wvw.ObjectiveOwner

/**
 * Transforms the image into the owner's color.
 */
class OwnedColorTransformation(config: Wvw, private val owner: ObjectiveOwner) : HexColorTransformation(owner.hex(config)) {
    private companion object {
        /**
         * @return the hex associated with the owner
         */
        fun ObjectiveOwner.hex(config: Wvw) = config.objectives.hex(this, "#888888")
    }

    override fun key(): String = owner.toString()
}