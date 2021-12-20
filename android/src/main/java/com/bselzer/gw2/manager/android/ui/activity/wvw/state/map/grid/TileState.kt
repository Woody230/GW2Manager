package com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.grid

data class TileState(
    val width: Int,
    val height: Int,
    val content: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TileState

        if (width != other.width) return false
        if (height != other.height) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + content.contentHashCode()
        return result
    }
}