package com.bselzer.gw2.manager.android.dialog

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.bselzer.gw2.manager.common.base.Dialog
import com.bselzer.gw2.manager.common.ui.composable.LocalState
import com.bselzer.gw2.v2.model.extension.world.worldId
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.ktx.compose.ui.dialog.SingleChoiceDialog
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.settings.compose.safeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorldSelectionDialog : Dialog {
    @Composable
    override fun Content(): Unit = LocalState.current.run {
        val worlds = remember { worlds.values }.sortedBy { world -> world.name }
        if (worlds.isEmpty()) {
            Toast.makeText(LocalContext.current, "There are no worlds to select from.", Toast.LENGTH_SHORT).show()
            return
        }

        var selectedId by wvwPref.selectedWorld.safeState()
        Logger.d("Dialog world id: $selectedId")

        // TODO preferably, choice should be scrolled to when dialog gets opened
        // TODO if current selected does not exist, do not allow cancellation
        val selected = remember { mutableStateOf<World?>(null) }
        selected.value = worlds.firstOrNull { world -> world.id == selectedId }
        SingleChoiceDialog(
            showDialog = { clearDialog() },
            title = "Worlds",
            values = worlds,
            labels = worlds.map { world -> world.name },
            selected = selected,
            onStateChanged = { world ->
                selectedId = world.id
                Logger.d("Set world to $world")

                // MUST not use remembered scope since it will be cancelled due to dialog closing.
                CoroutineScope(Dispatchers.IO).launch {
                    refreshWvwData(world.worldId())
                }
            }
        )
    }
}