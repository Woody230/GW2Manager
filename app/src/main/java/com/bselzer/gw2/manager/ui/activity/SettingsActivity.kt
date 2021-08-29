package com.bselzer.gw2.manager.ui.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bselzer.gw2.manager.R
import com.bselzer.gw2.manager.companion.AppCompanion
import com.bselzer.gw2.manager.companion.PreferenceCompanion.API_KEY
import com.bselzer.gw2.manager.ui.theme.AppTheme
import com.bselzer.library.gw2.v2.model.extension.token.ApiKey
import com.bselzer.library.kotlin.extension.preference.rememberNullString

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            supportActionBar?.let {
                it.title = stringResource(id = R.string.activity_settings)
                it.setDisplayHomeAsUpEnabled(true)
            }
            Content()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    @Preview
    @Composable
    private fun Content() = AppTheme {
        Background()
        Settings()
    }

    @Composable
    private fun Background() = Image(
        painter = painterResource(id = R.drawable.gw2_ice),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    @Composable
    private fun Settings() = Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.gw2_black_lion_key),
                contentDescription = null,
                modifier = Modifier.size(75.dp, 75.dp),
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = Modifier.size(10.dp))

            var keyText by remember { mutableStateOf("") }
            var apiKey by AppCompanion.PREF.rememberNullString(key = API_KEY.name)

            TextField(
                label = { TitleText(text = "API Key") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.Black, fontSize = 20.sp),
                onValueChange = { value ->
                    // Don't allow whitespace input.
                    val formatted = value.trim()
                    keyText = formatted

                    // Allow the user to clear stored keys with whitespace.
                    if (formatted.isBlank()) {
                        apiKey = null
                    } else if (ApiKey.isValid(formatted)) {
                        apiKey = formatted
                    }
                },

                // Using a password type to limit to letters/numbers.
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),

                // When the api key exists it must be valid so display it (necessary for initialization).
                value = if (apiKey.isNullOrBlank()) keyText else apiKey.orEmpty(),

                // Only display an error for partial or invalid characters.
                // TODO error text?
                isError = keyText.isNotBlank() && !ApiKey.isValid(keyText),
            )
        }

    }

    @Composable
    private fun TitleText(text: String) = Text(text = text, color = Color.Black, fontSize = 25.sp, fontWeight = FontWeight.Bold)
}