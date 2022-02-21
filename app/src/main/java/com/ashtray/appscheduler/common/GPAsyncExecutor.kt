package com.ashtray.appscheduler.common

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class GPAsyncExecutor {
    private val executor: Executor = Executors.newSingleThreadExecutor()

    fun executeAsync(runnable: Runnable?) {
        executor.execute(runnable)
    }
}