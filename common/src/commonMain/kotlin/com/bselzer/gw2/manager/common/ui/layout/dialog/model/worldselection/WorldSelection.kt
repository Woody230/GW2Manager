package com.bselzer.gw2.manager.common.ui.layout.dialog.model.worldselection

import com.bselzer.gw2.v2.model.world.World
import dev.icerock.moko.resources.desc.StringDesc

data class WorldSelection(
    val title: StringDesc,
    val values: List<World>,
    val getLabel: (World) -> String,
    val selected: World?,
    val onSave: (World) -> Unit,
    val onReset: () -> Unit,
    val setSelected: (World) -> Unit,
    val resetSelected: () -> Unit
)