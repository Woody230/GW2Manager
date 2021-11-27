package com.bselzer.library.kotlin.extension.coroutine

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Repeats the [block] every [interval].
 */
@OptIn(ExperimentalTime::class)
suspend fun CoroutineScope.repeat(interval: Duration, block: suspend CoroutineScope.() -> Unit) {
    while (true) {
        block(this)
        delay(interval)
    }
}

/**
 * Shows a Toast using the main coroutine context.
 */
suspend fun CoroutineScope.showToast(context: Context, text: CharSequence, duration: Int) = withContext(Dispatchers.Main) {
    Toast.makeText(context, text, duration).show()
}