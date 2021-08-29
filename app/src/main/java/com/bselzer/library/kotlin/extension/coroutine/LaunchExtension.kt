package com.bselzer.library.kotlin.extension.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun CoroutineScope.repeat(interval: Duration, block: suspend CoroutineScope.() -> Unit): Job = launch {
    while (true) {
        block(this)
        delay(interval)
    }
}