package com.bselzer.gw2.manager.android.ui.activity

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.ui.activity.common.BasePage
import com.bselzer.gw2.manager.common.expect.Gw2Aware
import com.bselzer.gw2.v2.cache.instance.ContinentCache
import com.bselzer.gw2.v2.cache.instance.GuildCache
import com.bselzer.gw2.v2.cache.instance.WorldCache
import com.bselzer.gw2.v2.cache.instance.WvwCache
import com.bselzer.ktx.compose.ui.appbar.DeleteButton
import com.bselzer.ktx.compose.ui.appbar.UpNavigationIcon
import com.bselzer.ktx.compose.ui.container.DividedColumn
import com.bselzer.ktx.compose.ui.preference.CheckBoxPreference
import com.bselzer.ktx.coroutine.showToast

/**
 * The page for managing underlying caches.
 */
class CachePage(
    aware: Gw2Aware,
    private val navigateUp: () -> Unit,
) : BasePage(aware) {
    private val selected = mutableStateListOf<CacheType>()

    private enum class CacheType {
        CONTINENT,
        GUILD,
        WVW,
        IMAGE
    }

    @Composable
    override fun Content() = RelativeBackgroundContent(
        backgroundModifier = Modifier.verticalScroll(rememberScrollState()),
        title = stringResource(R.string.activity_cache),
        navigationIcon = { UpNavigationIcon(onClick = navigateUp) },
        actions = {
            val context = LocalContext.current
            DeleteButton(enabled = selected.isNotEmpty()) {
                selected.forEach { type ->
                    when (type) {
                        CacheType.CONTINENT -> gw2Cache.apply {
                            get<ContinentCache>().clear()
                            tileCache.clear()
                        }
                        CacheType.IMAGE -> {
                            imageCache.clear()
                            tileCache.clear()
                        }
                        CacheType.GUILD -> gw2Cache.get<GuildCache>().clear()
                        CacheType.WVW -> gw2Cache.apply {
                            get<WvwCache>().clear()
                            get<WorldCache>().clear()
                        }
                    }
                }

                selected.clear()
                showToast(context, "Cache cleared.", Toast.LENGTH_SHORT)
            }
        }
    ) {
        DividedColumn(
            modifier = Modifier
                .padding(25.dp)
                .fillMaxSize(),
            divider = { Spacer(modifier = Modifier.height(25.dp)) },
            contents = arrayOf(
                {
                    CacheSection(
                        type = CacheType.CONTINENT,
                        painter = painterResource(id = R.drawable.gw2_gift_of_exploration),
                        title = "Continents",
                        subtitle = "Maps, regions, floors, tiles"
                    )
                },
                { CacheSection(type = CacheType.GUILD, painter = painterResource(id = R.drawable.gw2_guild_commendation), title = "Guilds", subtitle = "Upgrades") },
                { CacheSection(type = CacheType.IMAGE, painter = painterResource(id = R.drawable.gw2_sunrise), title = "Images", subtitle = "Icons, map tiles") },
                {
                    CacheSection(
                        type = CacheType.WVW,
                        painter = painterResource(id = R.drawable.gw2_rank_dolyak),
                        title = stringResource(R.string.activity_wvw),
                        subtitle = "Objectives, upgrades, worlds"
                    )
                },
            )
        )
    }

    @Composable
    private fun CacheSection(type: CacheType, painter: Painter, title: String, subtitle: String) = CheckBoxPreference(
        iconPainter = painter,
        title = title,
        subtitle = subtitle,
        checked = selected.contains(type),
        onStateChanged = { if (it) selected.add(type) else selected.remove(type) }
    )
}