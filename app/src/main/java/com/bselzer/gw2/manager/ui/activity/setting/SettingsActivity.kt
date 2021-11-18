package com.bselzer.gw2.manager.ui.activity.setting

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.bselzer.gw2.manager.R
import com.bselzer.gw2.manager.ui.kodein.DIAwareActivity
import com.bselzer.gw2.manager.ui.theme.AppTheme

class SettingsActivity : DIAwareActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            supportActionBar?.let {
                it.title = stringResource(id = R.string.activity_settings)
                it.setDisplayHomeAsUpEnabled(true)
            }
            AppTheme {
                Background()
            }
        }
        supportFragmentManager.beginTransaction().replace(android.R.id.content, SettingsFragment()).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            if (supportFragmentManager.backStackEntryCount != 0) {
                // Pop nested preference screen.
                supportFragmentManager.popBackStack()
            } else {
                // On the main preference screen so return to the previous activity.
                finish()
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    @Composable
    private fun Background() = Image(
        painter = painterResource(id = R.drawable.gw2_ice),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}