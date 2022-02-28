package com.ashtray.appscheduler.common

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ashtray.appscheduler.repository.MyRepository
import kotlinx.coroutines.*

@DelicateCoroutinesApi
class GPRebootListener: BroadcastReceiver() {
    companion object {
        private const val TAG = "[mg] GPRebootListener"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "onBroadcastReceived: phone rebooted")
        intent?.action?.equals("android.intent.action.BOOT_COMPLETED")?.let {
            performReScheduling(context)
        }
    }

    private fun performReScheduling(context: Context?) {
        GlobalScope.launch(Dispatchers.IO) {
            context?.let {
                Log.i(TAG, "onBroadcastReceive: setting alarms")
                val myList = MyRepository.getInstance(context).getRemainingTaskList()
                Log.i(TAG, "onBroadcastReceive: total items found ${myList.size}")
                withContext(Dispatchers.Main) {
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
                    for (item in myList) {
                        val eIntent = Intent(context, GPTaskExecutor::class.java).apply {
                            putExtra(GPConst.PK_APP_ID, item.pkgName)
                            putExtra(GPConst.PK_START_TIME, item.startTime.toString())
                        }
                        val pendingIntent = PendingIntent.getBroadcast(
                            context,
                            item.startTime.toInt(),
                            eIntent,
                            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                        )
                        alarmManager?.cancel(pendingIntent)
                        alarmManager?.setExact(
                            AlarmManager.RTC_WAKEUP,
                            item.startTime,
                            pendingIntent
                        )
                        Log.i(TAG, "onBroadcastReceive: alarm set for [${item.pkgName}]")
                        Log.i(TAG, "onBroadcastReceive: alarm set at [${item.startTime}]")
                        Log.i(TAG, "onBroadcastReceive: alarm set br id [${item.startTime.toInt()}]")
                    }
                }
                Log.i(TAG, "onBroadcastReceive: all items rescheduled")
            }
        }
    }
}