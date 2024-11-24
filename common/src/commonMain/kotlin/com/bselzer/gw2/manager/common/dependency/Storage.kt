package com.bselzer.gw2.manager.common.dependency

import com.bselzer.gw2.manager.common.storage.TileId
import com.bselzer.gw2.manager.common.storage.TranslationId
import com.bselzer.gw2.v2.client.model.Token
import com.bselzer.gw2.v2.intl.model.Translation
import com.bselzer.gw2.v2.model.account.token.TokenInfo
import com.bselzer.gw2.v2.model.continent.Continent
import com.bselzer.gw2.v2.model.continent.ContinentId
import com.bselzer.gw2.v2.model.continent.floor.Floor
import com.bselzer.gw2.v2.model.continent.floor.FloorId
import com.bselzer.gw2.v2.model.guild.Guild
import com.bselzer.gw2.v2.model.guild.GuildId
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgrade
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgradeId
import com.bselzer.gw2.v2.model.map.Map
import com.bselzer.gw2.v2.model.map.MapId
import com.bselzer.gw2.v2.model.tile.response.Tile
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.world.WorldId
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.model.wvw.match.WvwMatchId
import com.bselzer.gw2.v2.model.wvw.objective.WvwMapObjectiveId
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgradeId
import com.bselzer.ktx.serialization.storage.kottageSetStorage
import io.github.irgaly.kottage.Kottage

class Storage(
    kottage: Kottage
) {
    val continent = kottageSetStorage<ContinentId, Continent>(kottage, name = "Continent")
    val floor = kottageSetStorage<FloorId, Floor>(kottage, name = "Floor")
    val guild = kottageSetStorage<GuildId, Guild>(kottage, name = "Guild")
    val guildUpgrade = kottageSetStorage<GuildUpgradeId, GuildUpgrade>(kottage, name = "GuildUpgrade")
    val map = kottageSetStorage<MapId, Map>(kottage, name = "Map")
    val tile = kottageSetStorage<TileId, Tile>(kottage, name = "Tile")
    val tokenInfo = kottageSetStorage<Token, TokenInfo<*>>(kottage, name = "TokenInfo")
    val translation = kottageSetStorage<TranslationId, Translation>(kottage, name = "Translation")
    val world = kottageSetStorage<WorldId, World>(kottage, name = "World")
    val wvwMatch = kottageSetStorage<WvwMatchId, WvwMatch>(kottage, name = "WvwMatch")
    val wvwObjective = kottageSetStorage<WvwMapObjectiveId, WvwObjective>(kottage, name = "WvwObjective")
    val wvwUpgrade = kottageSetStorage<WvwUpgradeId, WvwUpgrade>(kottage, name = "WvwUpgrade")
}