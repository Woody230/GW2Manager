package com.bselzer.gw2.manager.common.ui.base

import com.bselzer.gw2.manager.common.dependency.ViewModelDependencies
import com.bselzer.gw2.manager.common.repository.data.generic.ColorData
import com.bselzer.gw2.manager.common.repository.data.generic.OwnerData
import com.bselzer.gw2.manager.common.repository.data.generic.TranslateData

abstract class ViewModel(context: AppComponentContext) : ViewModelDependencies,
    AppComponentContext by context,
    TranslateData by context.repositories.translation,
    ColorData by context.repositories.color,
    OwnerData by context.repositories.owner