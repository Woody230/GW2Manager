package com.bselzer.gw2.manager.ui.activity.common

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bselzer.gw2.manager.R
import com.bselzer.gw2.manager.ui.kodein.DIAwareActivity
import com.bselzer.gw2.manager.ui.theme.Theme
import com.bselzer.gw2.manager.ui.theme.appThemeType
import com.bselzer.library.kotlin.extension.compose.ui.ShowBackground

abstract class BaseActivity : DIAwareActivity() {
    /**
     * Displays an image suitable for a relative background based on the current theme. This is typically used for backgrounds that are covered by text.
     */
    @Composable
    protected fun ShowRelativeBackground() = ShowBackground(drawableId = relativeBackgroundDrawableId(), alignment = relativeBackgroundAlignment())

    /**
     * Displays an image suitable for the absolute background based on the current theme. This is typically used for backgrounds that are not covered by text.
     */
    @Composable
    protected fun ShowAbsoluteBackground() = ShowBackground(drawableId = absoluteBackgroundDrawableId())

    /**
     * Displays an image suitable for a relative background based on the current theme. This is typically used for backgrounds that are covered by text.
     */
    @Composable
    protected fun BoxScope.ShowRelativeBackground() = ShowBackground(drawableId = relativeBackgroundDrawableId(), alignment = relativeBackgroundAlignment())

    /**
     * Displays an image suitable for the absolute background based on the current theme. This is typically used for backgrounds that are not covered by text.
     */
    @Composable
    protected fun BoxScope.ShowAbsoluteBackground() = ShowBackground(drawableId = absoluteBackgroundDrawableId())

    @Composable
    private fun relativeBackgroundDrawableId() = if (appThemeType() == Theme.DARK) R.drawable.gw2_bloodstone_night else R.drawable.gw2_ice

    @Composable
    private fun relativeBackgroundAlignment() = if (appThemeType() == Theme.DARK) Alignment.TopCenter else Alignment.Center

    @Composable
    private fun absoluteBackgroundDrawableId() = R.drawable.gw2_two_sylvari

    @Composable
    protected fun ShowCenteredRow(startValue: String, endValue: String): Unit =
        com.bselzer.library.kotlin.extension.compose.ui.ShowCenteredRow(startValue = startValue, endValue = endValue, startTextStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold))

    /**
     * Displays a card for each of the [items].
     *
     * @param items the title mapped to the on-click handler
     */
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    protected fun ShowMenu(vararg items: Pair<String, () -> Unit>) =
        LazyColumn(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            itemsIndexed(items) { index, item ->
                Card(
                    onClick = item.second, elevation = 8.dp, modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                ) {
                    ShowRelativeBackground()
                    Text(
                        text = item.first,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.wrapContentSize()
                    )
                }

                // Add spacer in between items.
                if (index != items.size - 1) {
                    Spacer(Modifier.size(20.dp))
                }
            }
        }

    /**
     * Displays an up-navigation icon for an app bar that navigates up based on the [intent].
     */
    @Composable
    protected fun UpNavigationIcon(intent: Intent) = com.bselzer.library.kotlin.extension.compose.ui.UpNavigationIcon {
        navigateUpTo(intent)
        overridePendingTransition(0, 0)
    }

    /**
     * Displays a circular progress indicator.
     */
    @Composable
    protected fun ProgressIndicator() = CircularProgressIndicator(
        modifier = Modifier.fillMaxSize(0.15f)
    )
}