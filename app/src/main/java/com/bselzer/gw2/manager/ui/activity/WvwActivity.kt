package com.bselzer.gw2.manager.ui.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.bselzer.gw2.manager.R
import com.bselzer.gw2.manager.ui.theme.AppTheme

class WvwActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            supportActionBar?.let {
                it.title = stringResource(id = R.string.activity_wvw)
                it.setDisplayHomeAsUpEnabled(true)
            }
            Content()
        }
    }

    override fun onResume() {
        super.onResume()

        // TODO retrieve data
    }

    @Preview
    @Composable
    private fun Content() = AppTheme {
        // TODO layout
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}