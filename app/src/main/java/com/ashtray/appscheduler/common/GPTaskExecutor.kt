package com.ashtray.appscheduler.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class GPTaskExecutor: BroadcastReceiver() {
    companion object {
        private const val TAG = "[mg]GPTaskExecutor"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val sharedPreferences = context?.getSharedPreferences("hello", Context.MODE_PRIVATE)
        val scannedValue = sharedPreferences?.getString("hello2", "default_value") ?: "null_sp"
        Log.d(TAG, "scannedValue = $scannedValue")

        intent?.getStringExtra("app_id").let {
            Log.d(TAG, "appId = $it")
        }
    }
}