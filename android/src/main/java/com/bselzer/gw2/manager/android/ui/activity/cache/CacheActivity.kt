package com.bselzer.gw2.manager.android.ui.activity.cache

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.ui.activity.cache.CacheActivity.CacheType.*
import com.bselzer.gw2.manager.android.ui.activity.common.BaseActivity
import com.bselzer.gw2.manager.android.ui.activity.main.MainActivity
import com.bselzer.library.gw2.v2.cache.instance.ContinentCache
import com.bselzer.library.gw2.v2.cache.instance.GuildCache
import com.bselzer.library.gw2.v2.cache.instance.WorldCache
import com.bselzer.library.gw2.v2.cache.instance.WvwCache
import com.bselzer.library.kotlin.extension.compose.ui.appbar.DeleteButton
import com.bselzer.library.kotlin.extension.compose.ui.appbar.UpNavigationIcon
import com.bselzer.library.kotlin.extension.compose.ui.container.DividedColumn
import com.bselzer.library.kotlin.extension.compose.ui.preference.CheckBoxPreference
import com.bselzer.library.kotlin.extension.coroutine.showToast

class CacheActivity : BaseActivity() {
    private val selected = mutableStateListOf<CacheType>()

    private enum class CacheType {
        CONTINENT,
        GUILD,
        WVW,
    }

    @Composable
    override fun Content() = RelativeBackgroundContent(
        backgroundModifier = Modifier.verticalScroll(rememberScrollState()),
        title = stringResource(R.string.activity_cache),
        navigationIcon = { UpNavigationIcon(destination = MainActivity::class.java) },
        actions = {
            DeleteButton(enabled = selected.isNotEmpty()) {
                selected.forEach { type ->
                    when (type) {
                        CONTINENT -> gw2Cache.apply {
                            get<ContinentCache>().clear()
                            tileCache.clear()
                        }
                        GUILD -> gw2Cache.get<GuildCache>().clear()
                        WVW -> gw2Cache.apply {
                            get<WvwCache>().clear()
                            get<WorldCache>().clear()
                        }
                    }
                }

                selected.clear()
                showToast(this@CacheActivity, "Cache cleared.", Toast.LENGTH_SHORT)
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
                        type = CONTINENT,
                        painter = painterResource(id = R.drawable.gw2_gift_of_exploration),
                        title = "Continents",
                        subtitle = "Maps, regions, floors, tiles"
                    )
                },
                { CacheSection(type = GUILD, painter = painterResource(id = R.drawable.gw2_guild_commendation), title = "Guilds", subtitle = "Upgrades") },
                {
                    CacheSection(
                        type = WVW,
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