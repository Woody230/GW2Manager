package com.bselzer.gw2.manager.android.ui.kodein

import androidx.appcompat.app.AppCompatActivity
import coil.ImageLoader
import com.bselzer.gw2.manager.common.configuration.Configuration
import com.bselzer.gw2.manager.common.expect.AndroidApp
import com.bselzer.gw2.manager.common.preference.CommonPreference
import com.bselzer.gw2.manager.common.preference.WvwPreference
import com.bselzer.library.gw2.v2.cache.provider.Gw2CacheProvider
import com.bselzer.library.gw2.v2.client.client.Gw2Client
import com.bselzer.library.gw2.v2.emblem.client.EmblemClient
import com.bselzer.library.gw2.v2.tile.cache.instance.TileCache
import com.bselzer.library.gw2.v2.tile.client.TileClient
import org.kodein.db.DB
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance

/**
 * An activity with the DI dependencies provided.
 */
abstract class DIAwareActivity : AppCompatActivity(), DIAware {
    override val di: DI by closestDI()
    val app by instance<AndroidApp>()
    val database by instance<DB>()
    val gw2Client by instance<Gw2Client>()
    val gw2Cache by instance<Gw2CacheProvider>()
    val tileClient by instance<TileClient>()
    val tileCache by instance<TileCache>()
    val emblemClient by instance<EmblemClient>()
    val configuration by instance<Configuration>()
    val imageLoader by instance<ImageLoader>()
    val commonPref by instance<CommonPreference>()
    val wvwPref by instance<WvwPreference>()
}