package com.bselzer.gw2.manager.common.ui.layout.main.content.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.common.AbsoluteBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.main.content.map.objective.ObjectivePagerComposition
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ObjectiveViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
class ObjectiveComposition(model: ObjectiveViewModel) : ViewModelComposition<ObjectiveViewModel>(model) {

    // TODO standardize capitalization of text, particularly for anything from the api -- for example, for French fortified is not capitalized while secured/reinforced are
    @Composable
    override fun ObjectiveViewModel.Content(modifier: Modifier) = AbsoluteBackgroundImage(
        modifier = Modifier.fillMaxSize().then(modifier),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Pager()
        }
    }

    @Composable
    private fun ObjectiveViewModel.Pager() = ObjectivePagerComposition(
        model = this@Pager,
        state = rememberPagerState(),
        verticalScroll = rememberScrollState()
    ).Content(
        modifier = Modifier.fillMaxSize()
    )
}