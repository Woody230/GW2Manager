package com.bselzer.library.kotlin.extension.coroutine

import kotlinx.coroutines.Job

/**
 * Remove and cancel all of the jobs in the deque.
 */
fun ArrayDeque<Job>.cancel() {
    while (any()) {
        removeFirst().cancel()
    }
}