import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.bselzer.gw2.manager.common.expect.DesktopApp
import com.bselzer.ktx.compose.image.ui.LocalImageDispatcher
import kotlinx.coroutines.Dispatchers

fun main() {
    val app = DesktopApp()
    application {
        Window(onCloseRequest = ::exitApplication) {
            app.Content {
                CompositionLocalProvider(
                    LocalImageDispatcher provides Dispatchers.IO
                ) {
                    // TODO desktop version
                }
            }
        }
    }
}