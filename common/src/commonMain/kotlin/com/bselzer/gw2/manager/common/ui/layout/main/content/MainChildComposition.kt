package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.MainViewModel
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.layout.iconbutton.IconButtonInteractor
import com.bselzer.ktx.compose.ui.layout.text.TextInteractor
import com.bselzer.ktx.compose.ui.notification.snackbar.LocalSnackbarHostState
import kotlinx.coroutines.launch

abstract class MainChildComposition<Model>(model: Model) : ViewModelComposition<Model>(model) where Model : MainViewModel {
    /**
     * Creates the [TextInteractor] for the top app bar title.
     */
    val title
        @Composable
        get() = TextInteractor(text = model.title.localized())

    /**
     * Creates the [IconButtonInteractor]s for the top app bar buttons.
     */
    val actions: @Composable () -> List<IconButtonInteractor> = {
        val scope = rememberCoroutineScope()
        val host = LocalSnackbarHostState.current
        model.actions.map { action ->
            val notification = action.notification?.localized()
            IconButtonInteractor(
                enabled = action.enabled,
                icon = action.icon(),
                onClick = {
                    scope.launch {
                        action.onClick(this)

                        if (!notification.isNullOrBlank()) {
                            host.showSnackbar(message = notification)
                        }
                    }
                }
            )
        }
    }
}