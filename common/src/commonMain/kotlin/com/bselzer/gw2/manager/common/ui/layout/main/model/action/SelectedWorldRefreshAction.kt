package com.bselzer.gw2.manager.common.ui.layout.main.model.action

import androidx.compose.runtime.Composable
import com.bselzer.gw2.manager.common.repository.data.specialized.SelectedWorldData
import com.bselzer.ktx.compose.ui.layout.icon.IconInteractor
import com.bselzer.ktx.compose.ui.layout.icon.refreshIconInteractor
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.CoroutineScope

class SelectedWorldRefreshAction(data: SelectedWorldData) : AppBarAction {
    override val enabled: Boolean = true
    override val icon: @Composable () -> IconInteractor = { refreshIconInteractor() }
    override val notification: StringDesc? = null
    override val onClick: suspend CoroutineScope.() -> Unit = { data.forceRefresh() }

    companion object {
        fun SelectedWorldData.refreshAction() = SelectedWorldRefreshAction(this)
    }
}