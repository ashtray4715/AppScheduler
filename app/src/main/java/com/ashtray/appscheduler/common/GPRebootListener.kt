package com.ashtray.appscheduler.common

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
            context?.let { mContext ->
                Log.i(TAG, "performReScheduling: started")
                val myList = MyRepository.getInstance(mContext).getRemainingTaskList()
                Log.i(TAG, "performReScheduling: total items found ${myList.size}")
                for (item in myList) {
                    GPUtils().addSchedule(mContext, item)
                }
                Log.i(TAG, "performReScheduling: all done")
            }
        }
    }
}