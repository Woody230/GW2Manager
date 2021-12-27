package com.bselzer.gw2.manager.common.state.selected

data class SelectedDataState(
    // Titles mapped to values
    val pointsPerTick: Pair<String, String>,
    val pointsPerCapture: Pair<String, String>,
    val yaks: Pair<String, String>?,
    val upgrade: Pair<String, String>?
)