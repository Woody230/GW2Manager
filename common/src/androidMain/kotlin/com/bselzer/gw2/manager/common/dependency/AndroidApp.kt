package com.bselzer.gw2.manager.common.dependency

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.bselzer.gw2.manager.common.BuildConfig
import com.bselzer.ktx.logging.Logger
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.datastore.DataStoreSettings
import io.github.irgaly.kottage.KottageEnvironment
import io.github.irgaly.kottage.platform.contextOf
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpResponseValidator
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.http.RealResponseBody
import okio.Buffer

@OptIn(ExperimentalSettingsImplementation::class, ExperimentalSettingsImplementation::class, ExperimentalSettingsApi::class)
class AndroidApp(
    context: Context,
    scope: CoroutineScope,
    datastore: DataStore<Preferences>
) : App(
    debugMode = BuildConfig.DEBUG,
    scope = scope,
    httpClient = httpClient(),

    // TODO databaseDir extension
    databaseDirectory = context.getDatabasePath("temp").parent ?: throw Exception("Unable to get the database directory."),
    legacyDatabaseDirectory = context.filesDir.absolutePath,
    settings = DataStoreSettings(datastore),
    coilContext = context,
    kottageEnvironment = KottageEnvironment(
        context = contextOf(context),
    )
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
    }
}