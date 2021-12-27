package com.bselzer.gw2.manager.common.expect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.bselzer.gw2.manager.common.configuration.Configuration
import com.bselzer.gw2.manager.common.preference.CommonPreference
import com.bselzer.gw2.manager.common.preference.WvwPreference
import com.bselzer.gw2.manager.common.state.AppState
import com.bselzer.gw2.manager.common.ui.theme.AppTheme
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.gw2.v2.cache.provider.Gw2CacheProvider
import com.bselzer.gw2.v2.client.client.Gw2Client
import com.bselzer.gw2.v2.emblem.client.EmblemClient
import com.bselzer.gw2.v2.tile.cache.instance.TileCache
import com.bselzer.gw2.v2.tile.client.TileClient
import com.bselzer.ktx.compose.image.cache.instance.ImageCache
import com.bselzer.ktx.compose.image.ui.LocalImageCache
import kotlinx.coroutines.runBlocking
import com.bselzer.ktx.logging.Logger
import kotlinx.coroutines.Dispatchers
import org.kodein.db.DB
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

interface Gw2Aware : DIAware {
    val appState: AppState
    val database: DB
    val gw2Client: Gw2Client
    val gw2Cache: Gw2CacheProvider
    val tileClient: TileClient
    val tileCache: TileCache
    val imageCache: ImageCache
    val emblemClient: EmblemClient
    val configuration: Configuration
    val commonPref: CommonPreference
    val wvwPref: WvwPreference

    @Composable
    fun Content(content: @Composable () -> Unit)
}

/**
 * Creates a Gw2Aware object from the DI.
 */
fun DI.gw2Aware() = object : Gw2Aware {
    override val di: DI = this@gw2Aware
    override val appState = AppState(this)
    override val database by instance<DB>()
    override val gw2Client by instance<Gw2Client>()
    override val gw2Cache by instance<Gw2CacheProvider>()
    override val tileClient by instance<TileClient>()
    override val tileCache by instance<TileCache>()
    override val imageCache by instance<ImageCache>()
    override val emblemClient by instance<EmblemClient>()
    override val configuration by instance<Configuration>()
    override val commonPref by instance<CommonPreference>()
    override val wvwPref by instance<WvwPreference>()

    @Composable
    override fun Content(content: @Composable () -> Unit) {
        val imageCache by di.instance<ImageCache>()

        // Using runBlocking to avoid the initial theme changing because it is noticeable.
        val theme by commonPref.theme.observe().collectAsState(initial = runBlocking { commonPref.theme.get() })
        AppTheme(theme) {
            CompositionLocalProvider(
                LocalTheme provides theme,
                LocalImageCache provides imageCache,
            ) {
                content()
            }
        }
    }
}