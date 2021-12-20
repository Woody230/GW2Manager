package com.bselzer.gw2.manager.android.ui.activity.wvw.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.common.ImageState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.selected.ClaimState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.selected.DataState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.selected.WvwSelectedState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.selected.overview.OverviewState
import com.bselzer.gw2.manager.android.ui.coil.ColorTransformation
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.gw2.v2.emblem.client.EmblemClient
import com.bselzer.gw2.v2.emblem.request.EmblemRequestOptions
import com.bselzer.ktx.compose.ui.container.DividedColumn
import com.bselzer.ktx.compose.ui.unit.toDp

class WvwSelectedObjectivePage(
    theme: Theme,
    imageLoader: ImageLoader,
    appBarActions: @Composable RowScope.() -> Unit,
    state: WvwSelectedState,
    private val emblemClient: EmblemClient,
) : WvwPage<WvwSelectedState>(theme, imageLoader, appBarActions, state) {
    @Composable
    override fun Content() = Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar()

        AbsoluteBackground(modifier = Modifier.fillMaxSize()) {
            // TODO pager: main = details, left = upgrades, right = guild upgrades
            DetailedSelectedObjective()
        }
    }

    @Composable
    override fun topAppBarTitle(): String = stringResource(id = R.string.wvw_detailed_selected_objective)

    /**
     * Lays out the detailed selected objective information.
     */
    @Composable
    private fun DetailedSelectedObjective() = DividedColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        divider = { Spacer(modifier = Modifier.height(10.dp)) },
        prepend = true,
        append = true,
        contents = arrayOf(
            { state.image.value?.let { ObjectiveImage(it) } },
            { state.overview.value?.let { InfoCard { Overview(it) } } },
            { state.data.value?.let { InfoCard { Data(it) } } },
            { state.claim.value?.let { InfoCard { Claim(it) } } }
        )
    )

    /**
     * Lays out a card wrapping the underlying [content].
     */
    @Composable
    private fun InfoCard(content: @Composable BoxScope.() -> Unit) {
        val border = 3.dp
        Card(
            elevation = 10.dp,
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth(.80f)
                .wrapContentHeight()
                .border(width = border, color = Color.Black)
                .padding(all = border)
        ) {
            RelativeBackground(content = content)
        }
    }

    /**
     * Lays out the image of the selected objective.
     */
    @Composable
    private fun ObjectiveImage(image: ImageState) {
        // Unlike typical usage of size, it is treated as DP instead of PX.
        val density = LocalDensity.current
        val request = ImageRequest.Builder(LocalContext.current)
            .data(image.link)
            .size(width = density.run { image.width.dp.toPx().toInt() }, height = density.run { image.height.dp.toPx().toInt() })
            .transformations(ColorTransformation(image.color.toArgb()))
            .build()

        Image(
            painter = rememberImagePainter(request = request, imageLoader = imageLoader),
            contentDescription = image.description,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(width = image.width.dp, height = image.height.dp)
        )
    }

    /**
     * Lays out the overview of information about the selected objective.
     */
    @Composable
    private fun Overview(overview: OverviewState) = Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // TODO images alongside the text?
        Text(text = overview.name, textAlign = TextAlign.Center)

        overview.map?.let { map ->
            Text(text = map.name, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = map.color)
        }

        overview.owner?.let { owner ->
            Text(text = owner.name, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = owner.color)
        }

        overview.flipped?.let { flipped ->
            Text(text = flipped, textAlign = TextAlign.Center)
        }
    }

    /**
     * Lays out the data points such as point information and upgrade progress.
     */
    @Composable
    private fun Data(data: DataState) = Column {
        @Composable
        fun Pair<String, String>?.Row() {
            this?.let {
                BoldCenteredRow(startValue = first, endValue = second)
            }
        }

        listOf(
            data.pointsPerTick,
            data.pointsPerCapture,
            data.yaks,
            data.upgrade
        ).forEach { pair -> pair.Row() }
    }

    /**
     * Lays out the guild claim information.
     */
    @Composable
    private fun Claim(claim: ClaimState) = Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = claim.claimedAt, textAlign = TextAlign.Center)
        Text(text = claim.claimedBy, textAlign = TextAlign.Center)
        GuildImage(claim = claim)
    }

    /**
     * Lays out the emblem associated with the guild claiming the objective.
     */
    @Composable
    private fun GuildImage(claim: ClaimState) {
        val emblemRequest = emblemClient.requestEmblem(guildId = claim.id, size = claim.size, EmblemRequestOptions.MAXIMIZE_BACKGROUND_ALPHA)

        val coilRequest = ImageRequest.Builder(LocalContext.current)
            .data(emblemClient.emblemUrl(emblemRequest))
            .size(size = claim.size)
            .build()

        Image(
            painter = rememberImagePainter(coilRequest, imageLoader),
            contentDescription = claim.description,
            modifier = Modifier.size(size = claim.size.toDp())
        )
    }
}