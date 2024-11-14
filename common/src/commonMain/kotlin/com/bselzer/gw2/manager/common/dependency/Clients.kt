package com.bselzer.gw2.manager.common.dependency

import com.bselzer.gw2.asset.cdn.client.AssetCdnClient
import com.bselzer.gw2.v2.client.instance.Gw2Client
import com.bselzer.gw2.v2.client.instance.TileClient
import com.bselzer.gw2.v2.emblem.client.EmblemClient
import io.ktor.client.*

data class Clients(
    val http: HttpClient,
    val gw2: Gw2Client,
    val tile: TileClient,
    val emblem: EmblemClient,
    val asset: AssetCdnClient
)