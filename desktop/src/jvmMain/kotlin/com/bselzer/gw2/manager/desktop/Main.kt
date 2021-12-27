import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.bselzer.gw2.manager.common.expect.DesktopApp
import com.bselzer.gw2.manager.common.expect.gw2Aware
import com.bselzer.ktx.compose.image.ui.LocalImageDispatcher
import kotlinx.coroutines.Dispatchers

fun main() {
    val aware = DesktopApp().di.gw2Aware()
    application {
        Window(onCloseRequest = ::exitApplication) {
            aware.Content {
                CompositionLocalProvider(
                    LocalImageDispatcher provides Dispatchers.IO
                ) {
                    // TODO desktop version
                }
            }
        }
    }
}