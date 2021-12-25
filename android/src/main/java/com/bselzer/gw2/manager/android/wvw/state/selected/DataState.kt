package com.bselzer.gw2.manager.android.wvw.state.selected

data class DataState(
    // Titles mapped to values
    val pointsPerTick: Pair<String, String>,
    val pointsPerCapture: Pair<String, String>,
    val yaks: Pair<String, String>?,
    val upgrade: Pair<String, String>?
)