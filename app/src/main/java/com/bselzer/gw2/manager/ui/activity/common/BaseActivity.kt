package com.bselzer.gw2.manager.ui.activity.common

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bselzer.gw2.manager.ui.kodein.DIAwareActivity

abstract class BaseActivity : DIAwareActivity() {
    /**
     * Displays the background across the entirety of the parent.
     */
    @Composable
    protected fun ShowBackground(@DrawableRes drawableId: Int, alignment: Alignment = Alignment.Center) =
        ShowBackground(drawableId = drawableId, modifier = Modifier.fillMaxSize(), alignment = alignment)

    /**
     * Displays the background across the entirety of the parent.
     */
    // Need to use matchParentSize() so that the image does not participate in sizing and can just fill the resulting size.
    @Composable
    protected fun BoxScope.ShowBackground(@DrawableRes drawableId: Int, alignment: Alignment = Alignment.Center) =
        ShowBackground(drawableId = drawableId, modifier = Modifier.matchParentSize(), alignment = alignment)

    /**
     * Displays the background.
     */
    @Composable
    private fun ShowBackground(@DrawableRes drawableId: Int, modifier: Modifier, alignment: Alignment = Alignment.Center) = Image(
        painter = painterResource(id = drawableId),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        alignment = alignment
    )

    /**
     * Displays a card for each of the [items].
     *
     * @param background the id of the background drawable
     * @param items the title mapped to the on-click handler
     */
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    protected fun ShowMenu(@DrawableRes background: Int, vararg items: Pair<String, () -> Unit>) =
        LazyColumn(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            itemsIndexed(items) { index, item ->
                Card(
                    onClick = item.second, elevation = 8.dp, modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                ) {
                    Image(
                        painter = painterResource(id = background),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                    Text(
                        text = item.first,
                        color = Color.Black,
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
     * Displays an up-navigation icon for an app bar.
     */
    @Composable
    protected fun UpNavigationIcon(onClick: () -> Unit) = IconButton(onClick = onClick) {
        Icon(Icons.Filled.ArrowBack, contentDescription = "Up")
    }

    /**
     * Displays an up-navigation icon for an app bar that navigates up based on the [intent].
     */
    @Composable
    protected fun UpNavigationIcon(intent: Intent) = UpNavigationIcon {
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

    /**
     * Displays a row with values aligned from the center with [spacing].
     */
    @Composable
    protected fun ShowCenteredRow(startValue: String, endValue: String, spacing: Dp = 5.dp) = ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (left, spacer, right) = createRefs()
        Text(text = startValue, textAlign = TextAlign.Right, modifier = Modifier.constrainAs(left) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(spacer.start)
            width = Dimension.fillToConstraints
        })
        Spacer(modifier = Modifier
            .width(spacing)
            .constrainAs(spacer) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(left.end)
                end.linkTo(right.start)
            })
        Text(text = endValue, textAlign = TextAlign.Left, modifier = Modifier.constrainAs(right) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(spacer.end)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
        })
    }
}