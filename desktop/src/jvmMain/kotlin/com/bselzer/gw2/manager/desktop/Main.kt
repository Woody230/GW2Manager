import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.bselzer.gw2.manager.common.expect.DesktopApp

fun main() {
    val app = DesktopApp()
    application {
        Window(onCloseRequest = ::exitApplication) {
            app.Content {
                // TODO
            }
        }
    }
}