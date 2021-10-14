package com.bselzer.gw2.manager.configuration.wvw

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import com.bselzer.gw2.manager.R
import com.bselzer.library.gw2.v2.model.enumeration.wvw.ObjectiveOwner
import com.bselzer.library.gw2.v2.model.enumeration.wvw.ObjectiveOwner.*
import com.bselzer.library.gw2.v2.model.enumeration.wvw.ObjectiveType
import com.bselzer.library.gw2.v2.model.enumeration.wvw.ObjectiveType.*
import com.bselzer.library.kotlin.extension.function.ui.changeColor
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName(value = "Objective", namespace = "", prefix = "")
data class WvwMapObjective(
    /**
     * A user-friendly identifier for representing this objective within the map.
     */
    @XmlSerialName("code", "", "")
    val code: String = "",

    /**
     * The identifier. This is ONLY the objective id and not concatenated with the map id.
     */
    @XmlSerialName("id", "", "")
    val id: Int,

    /**
     * The type of objective.
     */
    @XmlSerialName("type", "", "")
    @XmlElement(false)
    val type: ObjectiveType
) {
    // TODO move this stuff out of here
    /**
     * The drawable id representing the neutral image of this objective.
     */
    @DrawableRes
    val neutralDrawable: Int = when (type) {
        // TODO use api/coil to manage images instead
        CAMP -> R.drawable.gw2_wvw_camp_gray
        CASTLE -> R.drawable.gw2_wvw_castle_gray
        KEEP -> R.drawable.gw2_wvw_keep_gray
        TOWER -> R.drawable.gw2_wvw_tower_gray
        else -> TODO()
    }

    /**
     * @return the painter for the image representing the owned image of this objective
     */
    fun ownedPainter(owner: ObjectiveOwner, resources: Resources): Painter {
        // TODO hex codes for more accurate colors matching in-game
        // TODO configurable
        val color = when (owner) {
            RED -> Color.RED
            BLUE -> Color.BLUE
            GREEN -> Color.GREEN
            NEUTRAL -> Color.GRAY
        }

        val bitmap = BitmapFactory.decodeResource(resources, neutralDrawable)
        return BitmapPainter(bitmap.changeColor(color).asImageBitmap())
    }
}