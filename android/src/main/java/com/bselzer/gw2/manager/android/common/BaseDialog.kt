package com.bselzer.gw2.manager.android.common

import androidx.compose.runtime.MutableState
import com.bselzer.gw2.manager.common.expect.Gw2Aware

abstract class BaseDialog(
    aware: Gw2Aware,
    protected val show: MutableState<Boolean>
) : Dialog, Gw2Aware by aware