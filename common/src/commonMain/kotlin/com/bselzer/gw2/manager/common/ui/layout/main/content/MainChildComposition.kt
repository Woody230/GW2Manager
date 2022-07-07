package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.AppBarAction
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.MainViewModel
import com.bselzer.ktx.compose.ui.layout.iconbutton.IconButtonInteractor
import com.bselzer.ktx.compose.ui.layout.snackbarhost.LocalSnackbarHostState
import com.bselzer.ktx.compose.ui.layout.text.TextInteractor
import com.bselzer.ktx.compose.ui.layout.text.textInteractor
import com.bselzer.ktx.resource.strings.localized
import kotlinx.coroutines.launch

abstract class MainChildComposition<Model>(model: Model) : ViewModelComposition<Model>(model) where Model : MainViewModel {
    /**
     * Creates the [TextInteractor] for the top app bar title.
     */
    val title: TextInteractor
        @Composable
        get() = model.title.textInteractor()

    /**
     * Creates the [IconButtonInteractor]s for the top app bar actions.
     */
    open val actions: @Composable () -> List<IconButtonInteractor> = { model.actions.interactors() }

    /**
     * Creates the [IconButtonInteractor]s for the given actions.
     */
    @Composable
    protected fun List<AppBarAction>.interactors(): List<IconButtonInteractor> {
        val scope = rememberCoroutineScope()
        val host = LocalSnackbarHostState.current
        return map { action ->
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