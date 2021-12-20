package com.bselzer.gw2.manager.common.expect

import android.app.Application
import android.content.ContextWrapper
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import coil.ImageLoader
import coil.util.CoilUtils
import com.bselzer.gw2.manager.common.BuildConfig
import com.bselzer.ktx.logging.Logger
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.datastore.DataStoreSettings
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.http.*
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.http.RealResponseBody
import okio.Buffer
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

class AndroidApp(private val application: Application, private val datastore: () -> DataStore<Preferences>) : App() {
    override val isDebug: Boolean = BuildConfig.DEBUG

    init {
        initialize()
    }

    override fun DI.MainBuilder.bindHttpClient() {
        bindSingleton {
            OkHttpClient.Builder()
                .cache(CoilUtils.createDefaultCache(instance()))
                .addInterceptor { chain ->
                    var request: Request? = null
                    try {
                        request = chain.request()
                        Logger.d("Intercepted ${request.url}")

                        val originalResponse = chain.proceed(request)
                        val isCacheableRequest = request.headers[HttpHeaders.UserAgent] != GW2_USER_AGENT

                        // Do not cache anything coming from the GW2/Tile clients. That should only be left to Kodein-DB.
                        if (isCacheableRequest) originalResponse else originalResponse.newBuilder().header("Cache-Control", "no-store").build()
                    } catch (ex: Exception) {
                        Logger.e(ex)

                        // Fake the response as needed.
                        request = request ?: Request.Builder().url("").build()
                        Response.Builder().code(0).protocol(Protocol.HTTP_2).body(RealResponseBody("text/plain", 0, Buffer())).request(request)
                            .message(ex.message ?: "").build()
                    }
                }
                .build()
        }
        bindSingleton {
            HttpClient(OkHttp) {
                engine {
                    // Set the bound OkHttpClient.
                    preconfigured = instance()
                }

                install(UserAgent) {
                    agent = GW2_USER_AGENT
                }

                HttpResponseValidator {
                    handleResponseException { ex -> Logger.e(ex) }
                }
            }
        }
    }

    override fun DI.MainBuilder.bundledConfiguration(): String {
        val context by instance<ContextWrapper>()
        return context.assets.open("Configuration.xml").bufferedReader(Charsets.UTF_8).use { reader -> reader.readText() }
    }

    override fun DI.MainBuilder.databaseDirectory(): String {
        val context by instance<ContextWrapper>()
        return context.filesDir.absolutePath
    }

    @OptIn(ExperimentalSettingsImplementation::class, ExperimentalSettingsImplementation::class, ExperimentalSettingsApi::class)
    override fun DI.MainBuilder.bindSettings() = bindSingleton { DataStoreSettings(datastore()) }

    override fun DI.MainBuilder.bindPlatform() {
        bindSingleton { this@AndroidApp }

        // Application / context
        bindSingleton { application }

        // Coil image loader
        // TODO cache using kodein-db instead
        bindSingleton { ImageLoader.Builder(instance()).okHttpClient(instance<OkHttpClient>()).build() }
    }
}