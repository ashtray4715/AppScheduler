package com.ashtray.appscheduler.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.ashtray.appscheduler.repository.MyRepository

class GPTaskExecutor: BroadcastReceiver() {
    companion object {
        private const val TAG = "[mg] GPTaskExecutor"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val appId = intent?.getStringExtra(GPConst.PK_APP_ID) ?: let {
            Log.e(TAG, "onBroadcastReceive: app id not found [RETURN]")
            return
        }
        val startTime = intent.getStringExtra(GPConst.PK_START_TIME) ?: let {
            Log.e(TAG, "onBroadcastReceive: start time not found [RETURN]")
            return
        }
        Log.i(TAG, "onBroadcastReceive: appId = $appId")
        Log.i(TAG, "onBroadcastReceive: startTime = $startTime")
        Log.i(TAG, "onBroadcastReceive: startTime = ${GPDateTime(startTime.toLong()).dateTimeString}")

        try {
            context?.startActivity(
                context.packageManager?.getLaunchIntentForPackage(appId)?.apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            )
            Log.i(TAG, "onBroadcastReceive: app opened successfully ")
            Toast.makeText(context, "app launched", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e(TAG, "onBroadcastReceive: app can't be opened")
            e.printStackTrace()
            Toast.makeText(context, "app not launched", Toast.LENGTH_LONG).show()
        }

        context?.let {
            Log.i(TAG, "onBroadcastReceive: updating database = $startTime")
            val myRepository = MyRepository.getInstance(context)
            myRepository.markTaskAsCompleted(startTime.toLong())
        }
        Log.i(TAG, "onBroadcastReceive: execution done")
    }
}