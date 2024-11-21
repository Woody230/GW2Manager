package com.bselzer.gw2.manager.common.dependency

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import coil3.PlatformContext
import com.bselzer.gw2.manager.AppDatabase
import com.bselzer.gw2.manager.BuildKonfig
import com.bselzer.ktx.logging.Logger
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.coroutines.toSuspendSettings
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.http.RealResponseBody
import okio.Buffer
import java.nio.file.Files
import java.util.prefs.Preferences
import kotlin.io.path.Path

@OptIn(ExperimentalSettingsApi::class)
class JvmApp : App(
    scope = CoroutineScope(Dispatchers.Main),
    httpClient = httpClient(),
    sqlDriver = sqlDriver(),
    settings = PreferencesSettings(Preferences.userRoot()).toSuspendSettings(),
    platformContext = PlatformContext.INSTANCE
) {
    private companion object {
        fun okHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                var request: Request? = null
                try {
                    request = chain.request()
                    Logger.d("Ktor | Intercepted ${request.url}")
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

        fun sqlDriver(): SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also { driver ->
            AppDatabase.Schema.create(driver)
        }
    }
}