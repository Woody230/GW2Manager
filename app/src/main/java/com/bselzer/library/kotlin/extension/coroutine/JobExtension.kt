package com.bselzer.library.kotlin.extension.coroutine

import kotlinx.coroutines.Job
import java.util.*

/**
 * Remove and cancel all of the jobs in the deque.
 */
fun Deque<Job>.cancel() {
    while (any()) {
        val job = pop()
        job.cancel()
    }
}