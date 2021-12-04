package com.bselzer.gw2.manager.common.expect

import com.bselzer.gw2.manager.common.configuration.Configuration
import com.bselzer.gw2.manager.common.preference.CommonPreference
import com.bselzer.gw2.manager.common.preference.WvwPreference
import com.bselzer.library.gw2.v2.cache.provider.Gw2CacheProvider
import com.bselzer.library.gw2.v2.client.client.Gw2Client
import com.bselzer.library.gw2.v2.emblem.client.EmblemClient
import com.bselzer.library.gw2.v2.tile.cache.instance.TileCache
import com.bselzer.library.gw2.v2.tile.client.TileClient
import org.kodein.db.DB
import org.kodein.di.DIAware

interface Gw2Aware<Gw2App : App> : DIAware {
    val app: Gw2App
    val database: DB
    val gw2Client: Gw2Client
    val gw2Cache: Gw2CacheProvider
    val tileClient: TileClient
    val tileCache: TileCache
    val emblemClient: EmblemClient
    val configuration: Configuration
    val commonPref: CommonPreference
    val wvwPref: WvwPreference
}