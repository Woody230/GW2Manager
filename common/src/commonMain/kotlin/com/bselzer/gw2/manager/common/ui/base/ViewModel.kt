package com.bselzer.gw2.manager.common.ui.base

import com.bselzer.gw2.manager.common.repository.instance.generic.TranslateData

abstract class ViewModel(context: AppComponentContext) : AppComponentContext by context, TranslateData by context.repositories.translation