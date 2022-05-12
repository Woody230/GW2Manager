package com.bselzer.gw2.manager.common.dependency

import com.bselzer.gw2.v2.cache.provider.Gw2CacheProvider
import com.bselzer.gw2.v2.tile.cache.instance.TileCache
import com.bselzer.ktx.compose.image.cache.instance.ImageCache
import org.kodein.db.DB

data class Caches(
    val database: DB,
    val gw2: Gw2CacheProvider,
    val tile: TileCache,
    val image: ImageCache
)