package com.bselzer.gw2.manager.android.ui.activity.common

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.ui.activity.main.MainActivity
import com.bselzer.gw2.manager.android.ui.kodein.DIAwareActivity
import com.bselzer.gw2.manager.android.ui.theme.Theme
import com.bselzer.gw2.manager.android.ui.theme.appThemeType
import com.bselzer.library.kotlin.extension.compose.ui.appbar.UpNavigationIcon
import com.bselzer.library.kotlin.extension.compose.ui.background.BackgroundImage
import com.bselzer.library.kotlin.extension.compose.ui.column.CenteredRow

abstract class BaseActivity : DIAwareActivity() {
    /**
     * Displays an image suitable for a relative background based on the current theme. This is typically used for backgrounds that are covered by text.
     */
    @Composable
    protected fun ShowRelativeBackground() = BackgroundImage(drawableId = relativeBackgroundDrawableId(), alignment = relativeBackgroundAlignment())

    /**
     * Displays an image suitable for the absolute background based on the current theme. This is typically used for backgrounds that are not covered by text.
     */
    @Composable
    protected fun ShowAbsoluteBackground() = BackgroundImage(drawableId = absoluteBackgroundDrawableId())

    /**
     * Displays an image suitable for a relative background based on the current theme. This is typically used for backgrounds that are covered by text.
     */
    @Composable
    protected fun BoxScope.ShowRelativeBackground() = BackgroundImage(drawableId = relativeBackgroundDrawableId(), alignment = relativeBackgroundAlignment())

    /**
     * Displays an image suitable for the absolute background based on the current theme. This is typically used for backgrounds that are not covered by text.
     */
    @Composable
    protected fun BoxScope.ShowAbsoluteBackground() = BackgroundImage(drawableId = absoluteBackgroundDrawableId())

    @Composable
    private fun relativeBackgroundDrawableId() = if (appThemeType() == Theme.DARK) R.drawable.gw2_bloodstone_night else R.drawable.gw2_ice

    @Composable
    private fun relativeBackgroundAlignment() = if (appThemeType() == Theme.DARK) Alignment.TopCenter else Alignment.Center

    @Composable
    private fun absoluteBackgroundDrawableId() = R.drawable.gw2_two_sylvari

    @Composable
    protected fun ShowCenteredRow(startValue: String, endValue: String, textSize: TextUnit? = null) {
        if (textSize == null) {
            CenteredRow(
                startValue = startValue,
                endValue = endValue,
                startTextStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold)
            )
        } else {
            CenteredRow(
                startValue = startValue,
                endValue = endValue,
                startTextStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold, fontSize = textSize),
                endTextStyle = LocalTextStyle.current.copy(fontSize = textSize)
            )
        }
    }

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
     * Displays an up-navigation icon for an app bar..
     */
    @Composable
    protected fun ShowUpNavigationIcon(destination: Class<out Activity> = MainActivity::class.java) = UpNavigationIcon(destination = destination) {
        navigateUpTo(intent)
        overridePendingTransition(0, 0)
    }

    /**
     * Displays a circular progress indicator.
     */
    @Composable
    protected fun ShowProgressIndicator() = CircularProgressIndicator(
        modifier = Modifier.fillMaxSize(0.15f)
    )
}