package com.bselzer.gw2.manager.common.dependency

import com.bselzer.gw2.manager.common.preference.CommonPreference
import com.bselzer.gw2.manager.common.preference.WvwPreference

data class Preferences(
    val common: CommonPreference,
    val wvw: WvwPreference
)