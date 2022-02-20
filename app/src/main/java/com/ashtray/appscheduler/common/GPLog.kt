package com.ashtray.appscheduler.common

import android.util.Log

object GPLog {

    private const val TAG = "[mg]"

    fun d(tag: String, message: String) {
        if (GPApplication.getInstance().enableLogPrinting()) {
            Log.d("$TAG[$tag]", message)
        }
    }

    fun e(tag: String, message: String) {
        if (GPApplication.getInstance().enableLogPrinting()) {
            Log.e("$TAG[$tag]", message)
        }
    }
}