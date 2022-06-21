package com.bselzer.gw2.manager.common.ui.base

import com.bselzer.gw2.manager.common.repository.data.generic.ColorData
import com.bselzer.gw2.manager.common.repository.data.generic.TranslateData

abstract class ViewModel(context: AppComponentContext) : AppComponentContext by context, TranslateData by context.repositories.translation,
    ColorData by context.repositories.color