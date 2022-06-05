package com.bselzer.gw2.manager.common.dependency

import com.bselzer.gw2.asset.cdn.client.AssetCdnClient
import com.bselzer.gw2.v2.client.instance.Gw2Client
import com.bselzer.gw2.v2.emblem.client.EmblemClient
import com.bselzer.gw2.v2.tile.client.TileClient
import com.bselzer.ktx.compose.image.client.ImageClient
import io.ktor.client.*

data class Clients(
    val http: HttpClient,
    val gw2: Gw2Client,
    val tile: TileClient,
    val image: ImageClient,
    val emblem: EmblemClient,
    val asset: AssetCdnClient
)