package com.bselzer.gw2.manager.common.expect

import com.bselzer.library.kotlin.extension.logging.Logger
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

class DesktopApp : App() {
    override val isDebug: Boolean = false

    init {
        initialize()
    }

    override fun DI.MainBuilder.bindHttpClient() {
        bindSingleton {
            // TODO caching mechanism
            OkHttpClient.Builder()
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

    override fun DI.MainBuilder.bundledConfiguration(): String = TODO("Not yet implemented")
    override fun DI.MainBuilder.databaseDirectory(): String = TODO("Not yet implemented")
    override fun DI.MainBuilder.bindSettings() = TODO("Not yet implemented")
    override fun DI.MainBuilder.bindPlatform() = TODO("Not yet implemented")
}