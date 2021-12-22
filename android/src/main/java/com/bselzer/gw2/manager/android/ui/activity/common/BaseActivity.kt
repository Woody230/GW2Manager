package com.bselzer.gw2.manager.android.ui.activity.common

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import coil.ImageLoader
import com.bselzer.gw2.manager.common.configuration.Configuration
import com.bselzer.gw2.manager.common.expect.AndroidApp
import com.bselzer.gw2.manager.common.expect.AndroidAware
import com.bselzer.gw2.manager.common.preference.CommonPreference
import com.bselzer.gw2.manager.common.preference.WvwPreference
import com.bselzer.gw2.v2.cache.provider.Gw2CacheProvider
import com.bselzer.gw2.v2.client.client.Gw2Client
import com.bselzer.gw2.v2.emblem.client.EmblemClient
import com.bselzer.gw2.v2.tile.cache.instance.TileCache
import com.bselzer.gw2.v2.tile.client.TileClient
import okhttp3.OkHttpClient
import org.kodein.db.DB
import org.kodein.di.DI
import org.kodein.di.android.closestDI
import org.kodein.di.instance

abstract class BaseActivity : AppCompatActivity(), AndroidAware {
    override val di: DI by closestDI()
    override val app by instance<AndroidApp>()
    override val database by instance<DB>()
    override val gw2Client by instance<Gw2Client>()
    override val gw2Cache by instance<Gw2CacheProvider>()
    override val tileClient by instance<TileClient>()
    override val tileCache by instance<TileCache>()
    override val emblemClient by instance<EmblemClient>()
    override val configuration by instance<Configuration>()
    override val commonPref by instance<CommonPreference>()
    override val wvwPref by instance<WvwPreference>()

    override val imageLoader by instance<ImageLoader>()
    override val okHttpClient by instance<OkHttpClient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            app.Content {
                Content()
            }
        }
    }

    @Composable
    protected abstract fun Content()
}