package com.bselzer.gw2.manager.ui.kodein

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.preference.PreferenceFragmentCompat
import coil.ImageLoader
import com.bselzer.gw2.manager.configuration.Configuration
import com.bselzer.library.gw2.v2.cache.provider.Gw2CacheProvider
import com.bselzer.library.gw2.v2.client.client.Gw2Client
import com.bselzer.library.gw2.v2.emblem.client.EmblemClient
import com.bselzer.library.gw2.v2.tile.cache.instance.TileCache
import com.bselzer.library.gw2.v2.tile.client.TileClient
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance

/**
 * A preference fragment with the DI dependencies provided.
 */
abstract class DIAwarePreferenceFragment : PreferenceFragmentCompat(), DIAware {
    override val di: DI by closestDI { requireContext() }
    val gw2Client by instance<Gw2Client>()
    val gw2Cache by instance<Gw2CacheProvider>()
    val tileClient by instance<TileClient>()
    val tileCache by instance<TileCache>()
    val emblemClient by instance<EmblemClient>()
    val datastore by instance<DataStore<Preferences>>()
    val configuration by instance<Configuration>()
    val imageLoader by instance<ImageLoader>()
}