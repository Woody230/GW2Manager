package com.bselzer.gw2.manager.android.ui.coil

import android.graphics.Bitmap
import coil.bitmap.BitmapPool
import coil.size.Size
import coil.transform.Transformation
import com.bselzer.ktx.function.ui.changeColor

/**
 * Transform the image into the given color.
 */
open class ColorTransformation(private val color: Int) : Transformation {
    override fun key(): String = color.toString()
    override suspend fun transform(pool: BitmapPool, input: Bitmap, size: Size): Bitmap = input.changeColor(color)
}