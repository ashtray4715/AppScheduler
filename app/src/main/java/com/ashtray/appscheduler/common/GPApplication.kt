package com.ashtray.appscheduler.common

import android.app.Application

import com.ashtray.appscheduler.R

class GPApplication: Application() {

    companion object {
        private const val TAG = "GPApplication"
        private lateinit var application: GPApplication

        fun getInstance(): GPApplication {
            return application
        }
    }

    private var enableLogPrinting = true

    override fun onCreate() {
        super.onCreate()
        application = this
        enableLogPrinting = getString(R.string.want_to_enable_log) == "yes"
        GPLog.d(TAG, "onCreate: called [enableLogPrinting=$enableLogPrinting]")
    }

    fun enableLogPrinting(): Boolean {
        return enableLogPrinting
    }


}