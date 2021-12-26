package com.bselzer.gw2.manager.android.other

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.common.NavigatePage
import com.bselzer.gw2.manager.common.expect.Gw2Aware
import com.bselzer.gw2.v2.model.enumeration.wvw.ObjectiveOwner
import com.bselzer.gw2.v2.model.extension.wvw.owner
import com.bselzer.ktx.compose.ui.preference.TextPreference
import com.bselzer.ktx.settings.compose.safeState

class ModulePage(
    aware: Gw2Aware,
    navigationIcon: @Composable () -> Unit
) : NavigatePage(aware, navigationIcon) {
    @Composable
    override fun background() = BackgroundType.ABSOLUTE

    @Composable
    override fun CoreContent() = Box(
        modifier = Modifier.padding(vertical = 25.dp)
    ) {
        // TODO modules: which worlds are assigned to each side for currently selected world, world selection, overview of WvW data, etc
        ModuleCard { SelectedWorld() }
    }

    @Composable
    override fun title(): String = stringResource(id = R.string.app_name)

    @Composable
    override fun contentAlignment(): Alignment = Alignment.TopCenter

    /**
     * Lays out a card wrapping the underlying [content].
     */
    @Composable
    private fun ModuleCard(content: @Composable BoxScope.() -> Unit) {
        val border = 3.dp
        Card(
            elevation = 10.dp,
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth(.90f)
                .wrapContentHeight()
                .border(width = border, color = Color.Black)
                .padding(all = border)
        ) {
            RelativeBackground(content = content)
        }
    }

    /**
     * Lays out the selected world with the ability to show the dialog for a new selection.
     */
    @Composable
    private fun SelectedWorld() {
        val selectedId by wvwPref.selectedWorld.safeState()
        val match = remember { appState.match }.value
        val worlds = remember { appState.worlds.values }
        val world = worlds.firstOrNull { world -> world.id == selectedId }
        val owner = world?.let { match?.owner(world) } ?: ObjectiveOwner.NEUTRAL
        val color = configuration.wvw.objectives.color(owner = owner)
        TextPreference(
            iconPainter = painterResource(id = R.drawable.gw2_rank_dolyak),
            title = "World",
            subtitle = if (selectedId <= 0) "Not set" else world?.name ?: "Unknown",
            subtitleStyle = MaterialTheme.typography.subtitle1.copy(color = color, fontWeight = FontWeight.Bold),
            onClick = { appState.showWorldDialog.value = true }
        )
    }
}