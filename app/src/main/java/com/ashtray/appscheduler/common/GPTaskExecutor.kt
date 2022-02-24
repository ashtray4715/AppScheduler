package com.ashtray.appscheduler.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class GPTaskExecutor: BroadcastReceiver() {
    companion object {
        private const val TAG = "[mg] GPTaskExecutor"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val appId = intent?.getStringExtra(GPConst.PK_APP_ID) ?: let {
            Log.e(TAG, "onBroadcastReceive: app id not found")
            return
        }
        val startTime = intent.getStringExtra(GPConst.PK_START_TIME) ?: let {
            Log.e(TAG, "onBroadcastReceive: start time not found")
            return
        }
        Log.i(TAG, "onBroadcastReceive: appId = $appId")
        Log.i(TAG, "onBroadcastReceive: startTime = $startTime")
        Log.i(TAG, "onBroadcastReceive: startTime = ${GPDateTime(startTime.toLong()).dateTimeString}")

        try {
            val launcherIntent = context?.packageManager?.getLaunchIntentForPackage(appId)
            launcherIntent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context?.startActivity(launcherIntent)
            Log.i(TAG, "onBroadcastReceive: app opened successfully ${launcherIntent != null}")
        } catch (e: Exception) {
            Log.e(TAG, "onBroadcastReceive: app can't be opened")
            e.printStackTrace()
        }

    }
}