import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.bselzer.gw2.manager.common.expect.App
import com.bselzer.gw2.manager.common.expect.DesktopApp
import org.kodein.di.DI

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