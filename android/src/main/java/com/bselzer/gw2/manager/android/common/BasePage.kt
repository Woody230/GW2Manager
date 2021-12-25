package com.bselzer.gw2.manager.android.common

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
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.common.expect.Gw2Aware
import com.bselzer.gw2.manager.common.expect.LocalTheme
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.ktx.compose.ui.background.Background
import com.bselzer.ktx.compose.ui.background.BackgroundColumn
import com.bselzer.ktx.compose.ui.background.BackgroundImage
import com.bselzer.ktx.compose.ui.container.CenteredRow

abstract class BasePage(
    aware: Gw2Aware
) : Page, Gw2Aware by aware {
    enum class BackgroundType {
        RELATIVE,
        ABSOLUTE,
        NONE
    }

    private val relative: @Composable () -> Unit = { BackgroundImage(drawableId = relativeBackgroundDrawableId(), alignment = relativeBackgroundAlignment()) }
    private val absolute: @Composable () -> Unit = { BackgroundImage(drawableId = absoluteBackgroundDrawableId()) }
    private val relativeBox: @Composable BoxScope.() -> Unit = { BackgroundImage(drawableId = relativeBackgroundDrawableId(), alignment = relativeBackgroundAlignment()) }
    private val absoluteBox: @Composable BoxScope.() -> Unit = { BackgroundImage(drawableId = absoluteBackgroundDrawableId()) }

    /**
     * Lays out an image suitable for a relative background based on the current theme. This is typically used for backgrounds that are covered by text.
     */
    @Composable
    protected fun RelativeBackground(modifier: Modifier = Modifier, contentAlignment: Alignment = Alignment.TopStart, content: @Composable BoxScope.() -> Unit) =
        Background(modifier = modifier, contentAlignment = contentAlignment, background = relativeBox, content = content)

    /**
     * Lays out an image suitable for the absolute background based on the current theme. This is typically used for backgrounds that are not covered by text.
     */
    @Composable
    protected fun AbsoluteBackground(modifier: Modifier = Modifier, contentAlignment: Alignment = Alignment.TopStart, content: @Composable BoxScope.() -> Unit) =
        Background(modifier = modifier, contentAlignment = contentAlignment, background = absoluteBox, content = content)

    @Composable
    protected fun RelativeBackgroundColumn(
        modifier: Modifier = Modifier,
        alignment: Alignment = Alignment.Center,
        contentHorizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
        contentModifier: Modifier = Modifier,
        content: @Composable ColumnScope.() -> Unit
    ) = BackgroundColumn(
        modifier = modifier,
        background = relativeBox,
        alignment = alignment,
        contentModifier = contentModifier,
        contentHorizontalAlignment = contentHorizontalAlignment,
        content = content
    )

    @Composable
    protected fun AbsoluteBackgroundColumn(
        modifier: Modifier = Modifier,
        alignment: Alignment = Alignment.Center,
        contentModifier: Modifier = Modifier,
        contentHorizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
        content: @Composable ColumnScope.() -> Unit
    ) = BackgroundColumn(
        modifier = modifier,
        background = { absolute() },
        alignment = alignment,
        contentModifier = contentModifier,
        contentHorizontalAlignment = contentHorizontalAlignment,
        content = content
    )

    @Composable
    private fun relativeBackgroundDrawableId() = if (LocalTheme.current == Theme.DARK) R.drawable.gw2_bloodstone_night else R.drawable.gw2_ice

    @Composable
    private fun relativeBackgroundAlignment() = if (LocalTheme.current == Theme.DARK) Alignment.TopCenter else Alignment.Center

    @Composable
    private fun absoluteBackgroundDrawableId() = R.drawable.gw2_two_sylvari

    @Composable
    protected fun BoldCenteredRow(startValue: String, endValue: String) = CenteredRow(
        startValue = startValue,
        endValue = endValue,
        startTextStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold)
    )

    /**
     * Lays out a card for each of the [items].
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
                    relative()
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
     * Lays out a circular progress indicator.
     */
    @Composable
    protected fun ProgressIndicator() = CircularProgressIndicator(
        modifier = Modifier.fillMaxSize(0.15f)
    )
}