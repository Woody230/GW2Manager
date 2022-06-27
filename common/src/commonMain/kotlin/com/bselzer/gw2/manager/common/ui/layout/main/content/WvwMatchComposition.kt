package com.bselzer.gw2.manager.common.ui.layout.main.content

import com.bselzer.gw2.manager.common.ui.layout.borderlands.content.BorderlandsComposition
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.WvwMatchViewModel

sealed class WvwMatchComposition<Model : WvwMatchViewModel<Data>, Data>(
    model: Model
) : MainChildComposition<Model>(model), BorderlandsComposition<Model, Data>