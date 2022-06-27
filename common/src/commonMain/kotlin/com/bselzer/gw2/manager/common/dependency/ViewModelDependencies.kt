package com.bselzer.gw2.manager.common.dependency

import com.bselzer.gw2.manager.common.repository.data.generic.ColorData
import com.bselzer.gw2.manager.common.repository.data.generic.OwnerData
import com.bselzer.gw2.manager.common.repository.data.generic.TranslateData
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext

interface ViewModelDependencies : AppComponentContext, TranslateData, ColorData, OwnerData