package com.bselzer.gw2.manager.ui.coil

import android.graphics.Bitmap
import android.graphics.Color
import coil.bitmap.BitmapPool
import coil.size.Size
import coil.transform.Transformation
import com.bselzer.library.kotlin.extension.function.ui.changeColor

/**
 * Transform the image into the given hex color.
 */
open class HexColorTransformation(private val hex: String): Transformation
{
    override fun key(): String = hex
    override suspend fun transform(pool: BitmapPool, input: Bitmap, size: Size): Bitmap {
        val color = if (hex.isBlank()) Color.GRAY else Color.parseColor(hex)
        return input.changeColor(color)
    }
}