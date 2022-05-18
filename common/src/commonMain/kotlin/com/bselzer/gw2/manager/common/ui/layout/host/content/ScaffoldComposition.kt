package com.bselzer.gw2.manager.common.ui.layout.host.content

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.host.viewmodel.ScaffoldViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.content.MainComposition
import com.bselzer.ktx.compose.ui.layout.scaffold.ScaffoldInteractor
import com.bselzer.ktx.compose.ui.layout.scaffold.ScaffoldPresenter
import com.bselzer.ktx.compose.ui.layout.scaffold.ScaffoldProjector

class ScaffoldComposition : ViewModelComposition<ScaffoldViewModel>() {
    @Composable
    override fun Content(model: ScaffoldViewModel) = model.run {
        val drawer = DrawerComposition(drawer)
        ScaffoldProjector(
            interactor = ScaffoldInteractor(
                drawer = drawer.interactor(),
            ),
            presenter = ScaffoldPresenter(
                drawer = drawer.presenter()
            )
        ).Projection(modifier = Modifier.fillMaxSize()) {
            MainComposition().Content()
        }
    }
}