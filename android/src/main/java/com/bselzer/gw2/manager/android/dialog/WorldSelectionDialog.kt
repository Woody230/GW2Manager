package com.bselzer.gw2.manager.android.dialog

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.bselzer.gw2.manager.android.common.BaseDialog
import com.bselzer.gw2.manager.common.expect.Gw2Aware
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.ktx.compose.ui.dialog.SingleChoiceDialog
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.settings.compose.safeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorldSelectionDialog(
    aware: Gw2Aware,
    show: MutableState<Boolean> = aware.appState.showWorldDialog
) : BaseDialog(aware, show) {
    @Composable
    override fun Content() {
        val worlds = remember { appState.worlds.values }.sortedBy { world -> world.name }
        if (worlds.isEmpty()) {
            Toast.makeText(LocalContext.current, "There are no worlds to select from.", Toast.LENGTH_SHORT).show()
            return
        }

        var selectedId by wvwPref.selectedWorld.safeState()
        Logger.d("Selected world id: $selectedId")


        // TODO preferably, choice should be scrolled to when dialog gets opened
        // TODO if current selected does not exist, do not allow cancellation
        val selected = remember { mutableStateOf<World?>(null) }
        selected.value = worlds.firstOrNull { world -> world.id == selectedId }
        SingleChoiceDialog(
            showDialog = { show.value = it },
            title = "Worlds",
            values = worlds,
            labels = worlds.map { world -> world.name },
            selected = selected,
            onStateChanged = { world ->
                selectedId = world.id

                // MUST not use remembered scope since it will be cancelled due to dialog closing.
                CoroutineScope(Dispatchers.IO).launch {
                    appState.refreshWvwData(world.id)
                }
            }
        )
    }
}