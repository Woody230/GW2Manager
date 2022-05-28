package com.bselzer.gw2.manager.common.ui.layout.main.model.action

import androidx.compose.runtime.Composable
import com.bselzer.ktx.compose.ui.layout.icon.IconInteractor
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.CoroutineScope

interface AppBarAction {
    /**
     * Whether the user is able to perform the action.
     */
    val enabled: Boolean

    /**
     * The notification to display after performing the action.
     */
    val notification: StringDesc?

    /**
     * The interactor for the icon's painter and content description.
     */
    val icon: @Composable () -> IconInteractor

    /**
     * The logic for performing the action.
     */
    val onClick: suspend CoroutineScope.() -> Unit
}