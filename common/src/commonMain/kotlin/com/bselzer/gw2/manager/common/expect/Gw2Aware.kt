package com.bselzer.gw2.manager.common.expect

import com.bselzer.gw2.manager.common.configuration.Configuration
import com.bselzer.gw2.manager.common.preference.CommonPreference
import com.bselzer.gw2.manager.common.preference.WvwPreference
import com.bselzer.gw2.manager.common.state.AppState
import com.bselzer.gw2.v2.cache.provider.Gw2CacheProvider
import com.bselzer.gw2.v2.client.client.Gw2Client
import com.bselzer.gw2.v2.emblem.client.EmblemClient
import com.bselzer.gw2.v2.tile.cache.instance.TileCache
import com.bselzer.gw2.v2.tile.client.TileClient
import com.bselzer.ktx.compose.image.cache.instance.ImageCache
import org.kodein.db.DB
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

interface Gw2Aware : DIAware {
    val app: App
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
}

/**
 * Creates a Gw2Aware object from the DI.
 */
fun DI.gw2Aware() = object : Gw2Aware {
    override val di: DI = this@gw2Aware
    override val app by instance<App>()
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
}