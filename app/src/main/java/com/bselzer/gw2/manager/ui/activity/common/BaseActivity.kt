package com.bselzer.gw2.manager.ui.activity.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.bselzer.gw2.manager.ui.kodein.DIAwareActivity

abstract class BaseActivity : DIAwareActivity() {
    /**
     * Displays the background across the entirety of the parent.
     */
    @Composable
    protected fun ShowBackground(@DrawableRes drawableId: Int) = Image(
        painter = painterResource(id = drawableId),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}