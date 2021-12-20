package com.bselzer.gw2.manager.android.ui.activity.wvw.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.match.ChartState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.match.WvwMatchState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.match.description.ChartDataState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.match.description.ChartDescriptionState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.match.pie.ChartBackgroundState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.match.pie.ChartDividerState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.match.pie.ChartSliceState
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.ktx.compose.ui.container.DividedColumn
import com.bselzer.ktx.compose.ui.geometry.ArcShape
import com.bselzer.ktx.compose.ui.unit.toDp

class WvwMatchPage(
    theme: Theme,
    imageLoader: ImageLoader,
    appBarActions: @Composable RowScope.() -> Unit,
    state: WvwMatchState,
) : WvwPage<WvwMatchState>(theme, imageLoader, appBarActions, state) {
    @Composable
    override fun Content() = Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar()

        // TODO pager: main = total, then for each map (will need map name title on each page)
        AbsoluteBackground(modifier = Modifier.fillMaxSize()) {
            DividedColumn(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                divider = { Spacer(modifier = Modifier.height(5.dp)) },
                contents = state.charts.value.map { chart -> pieChart(chart) }.toTypedArray()
            )
        }
    }

    @Composable
    override fun topAppBarTitle(): String = stringResource(id = R.string.wvw_match)

    /**
     * Lays out a pie chart.
     */
    @Composable
    private fun pieChart(chart: ChartState): @Composable ColumnScope.() -> Unit = {
        Box {
            ChartBackground(background = chart.background)
            chart.slices.forEach { slice -> ChartSlice(slice) }
            ChartDividers(divider = chart.divider, angles = chart.slices.map { slice -> slice.startAngle })
        }

        ChartDescription(description = chart.description)
    }

    /**
     * Lays out the pie chart background.
     */
    @Composable
    private fun ChartBackground(background: ChartBackgroundState) {
        val shadow = ImageRequest.Builder(LocalContext.current)
            .data(background.shadowLink)
            .size(width = background.width, height = background.height)
            .build()

        Image(
            painter = rememberImagePainter(request = shadow),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(width = background.width.toDp(), height = background.height.toDp())
        )

        val neutral = ImageRequest.Builder(LocalContext.current)
            .data(background.neutralLink)
            .size(width = background.width, height = background.height)
            .build()

        Image(
            painter = rememberImagePainter(request = neutral),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(width = background.width.toDp(), height = background.height.toDp())
        )
    }

    /**
     * Lays out a slice of the pie chart.
     */
    @Composable
    private fun ChartSlice(slice: ChartSliceState) {
        val request = ImageRequest.Builder(LocalContext.current)
            .data(slice.link)
            .size(slice.width, slice.height)
            .build()

        Image(
            painter = rememberImagePainter(request = request),
            contentDescription = slice.description,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(slice.width.toDp(), slice.height.toDp())
                .clip(ArcShape(slice.startAngle, slice.endAngle))
        )
    }

    /**
     * Lays out dividers along the given [angles].
     */
    @Composable
    private fun ChartDividers(divider: ChartDividerState, angles: Collection<Float>) = angles.forEach { angle ->
        val request = ImageRequest.Builder(LocalContext.current)
            .data(divider.link)
            .size(divider.width, divider.height)
            .build()

        Image(
            painter = rememberImagePainter(request = request),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(divider.width.toDp(), divider.height.toDp())
                .rotate(angle)
        )
    }

    /**
     * Lays out a description of the chart with its associated data.
     */
    @Composable
    private fun ChartDescription(description: ChartDescriptionState) = RelativeBackgroundColumn(
        modifier = Modifier.fillMaxWidth()
    )
    {
        Text(text = description.title.title, fontWeight = FontWeight.Bold, fontSize = description.title.size, textAlign = TextAlign.Center)

        // Show the data representing each slice.
        description.data.forEach { data -> ChartData(data) }
    }

    /**
     * Lays out the data associated with a slice.
     */
    @Composable
    private fun ChartData(data: ChartDataState) {
        Text(
            text = data.owner,
            fontWeight = FontWeight.Bold,
            fontSize = data.textSize,
            color = data.color,
            textAlign = TextAlign.Center
        )
        Text(text = data.data, fontSize = data.textSize, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(3.dp))
    }
}