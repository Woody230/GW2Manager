package com.bselzer.gw2.manager.ui.activity.setting

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.res.stringResource
import com.bselzer.gw2.manager.R
import com.bselzer.gw2.manager.ui.activity.common.BaseActivity
import com.bselzer.gw2.manager.ui.theme.AppTheme
import com.bselzer.library.kotlin.extension.compose.ui.ShowBackground

class SettingsActivity : BaseActivity() {
    // TODO DB clearing
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            supportActionBar?.let {
                it.title = stringResource(id = R.string.activity_settings)
                it.setDisplayHomeAsUpEnabled(true)
            }
            AppTheme {
                // TODO Since the preference components are not composable, determine based on system light/dark.
                ShowBackground(drawableId = if (isSystemInDarkTheme()) R.drawable.gw2_bloodstone_night else R.drawable.gw2_ice)
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
}