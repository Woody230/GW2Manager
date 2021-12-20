package com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.objective

interface IndicatorState {
    val enabled: Boolean
    val link: String?
    val description: String
    val width: Int
    val height: Int
}