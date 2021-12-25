package com.bselzer.gw2.manager.android.other

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.android.BuildConfig
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.common.NavigatePage
import com.bselzer.gw2.manager.common.expect.Gw2Aware
import com.bselzer.ktx.compose.ui.container.DividedColumn
import com.bselzer.ktx.compose.ui.description.Description

/**
 * The page for laying out information about the app.
 */
class AboutPage(
    aware: Gw2Aware,
    navigationIcon: @Composable () -> Unit,
) : NavigatePage(aware, navigationIcon) {
    @Composable
    override fun background() = BackgroundType.RELATIVE

    @Composable
    override fun CoreContent() = DividedColumn(
        modifier = Modifier.padding(all = 25.dp),
        divider = { Spacer(modifier = Modifier.height(25.dp)) },
        contents = arrayOf(
            {
                Description(
                    title = "Version",
                    subtitle = BuildConfig.VERSION_NAME
                )
            },
            {
                Description(
                    title = "Legal Notice",
                    subtitle = "© 2021 NCSOFT Corporation. All rights reserved. NCSOFT, ArenaNet, the interlocking NC logo, Aion, Lineage II, Guild Wars, Guild Wars 2: Heart of Thorns, Guild Wars 2: Path of Fire, Blade & Soul, and all associated logos, designs, and composite marks are trademarks or registered trademarks of NCSOFT Corporation. All other trademarks are the property of their respective owners."
                )
            }
        )
    )

    @Composable
    override fun title(): String = stringResource(id = R.string.app_name)

    override fun Modifier.background(): Modifier = composed { verticalScroll(rememberScrollState()) }
}