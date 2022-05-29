package com.bselzer.gw2.manager.common.ui.layout.main.model.action

import androidx.compose.runtime.Composable
import com.bselzer.gw2.manager.common.repository.instance.specialized.SelectedWorldRepository
import com.bselzer.ktx.compose.resource.ui.layout.icon.refreshIconInteractor
import com.bselzer.ktx.compose.ui.layout.icon.IconInteractor
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.CoroutineScope

class SelectedWorldRefreshAction(repository: SelectedWorldRepository) : AppBarAction {
    override val enabled: Boolean = true
    override val icon: @Composable () -> IconInteractor = { refreshIconInteractor() }
    override val notification: StringDesc? = null
    override val onClick: suspend CoroutineScope.() -> Unit = { repository.forceRefresh() }
}
