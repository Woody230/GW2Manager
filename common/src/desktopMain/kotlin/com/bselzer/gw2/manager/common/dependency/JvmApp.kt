package com.bselzer.gw2.manager.common.dependency

import com.bselzer.gw2.manager.common.Gw2Resources
import com.bselzer.ktx.logging.Logger
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.JvmPreferencesSettings
import com.russhwolf.settings.coroutines.toSuspendSettings
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.http.RealResponseBody
import okio.Buffer
import java.io.File
import java.nio.file.Files
import java.util.prefs.Preferences
import kotlin.io.path.Path

@OptIn(ExperimentalSettingsImplementation::class, ExperimentalSettingsApi::class)
class JvmApp : App(
    isDebug = false,
    configurationContent = Gw2Resources.assets.Configuration.readText(),
    httpClient = httpClient(),
    databaseDirectory = databaseDirectory(),
    settings = JvmPreferencesSettings(Preferences.userRoot()).toSuspendSettings(),
    libraryContent = Gw2Resources.assets.aboutlibraries.readText(),
) {
    private companion object {
        fun databaseDirectory(): String {
            val directory = Path(System.getProperty("user.dir") + File.pathSeparator + "AppData")
            if (Files.notExists(directory)) {
                Files.createDirectory(directory)
            }
            return directory.toString()
        }

        fun okHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                var request: Request? = null
                try {
                    request = chain.request()
                    Logger.d("Intercepted ${request.url}")
                    chain.proceed(request)
                } catch (ex: Exception) {
                    Logger.e(ex)

                    // Fake the response as needed in order to keep processing.
                    request = request ?: Request.Builder().url("").build()
                    Response.Builder().code(0).protocol(Protocol.HTTP_2).body(RealResponseBody("text/plain", 0, Buffer())).request(request)
                        .message(ex.message ?: "").build()
                }
            }
            .build()

        fun httpClient(): HttpClient = HttpClient(OkHttp) {
            engine {
                preconfigured = okHttpClient()
            }

            HttpResponseValidator {
                handleResponseExceptionWithRequest { cause, request -> Logger.e(cause) }
            }
        }
    }
}