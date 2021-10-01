package com.bselzer.library.kotlin.extension.coroutine

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Repeats the [block] every [interval].
 */
@OptIn(ExperimentalTime::class)
fun CoroutineScope.repeat(interval: Duration, block: suspend CoroutineScope.() -> Unit): Job = launch {
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