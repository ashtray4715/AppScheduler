package com.ashtray.appscheduler.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.ashtray.appscheduler.repository.MyRepository
import kotlinx.coroutines.*

class GPTaskExecutor: BroadcastReceiver() {
    companion object {
        private const val TAG = "[mg] GPTaskExecutor"
    }

    @DelicateCoroutinesApi
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "onBroadcastReceive: execution started")
        val taskId = intent?.getIntExtra(GPConst.PK_TASK_ID, -1) ?: let {
            Log.e(TAG, "onBroadcastReceive: task id not found [RETURN]")
            return
        }
        context?.let { mContext ->
            startExecuting(mContext, taskId)
        } ?: let {
            Log.e(TAG, "onBroadcastReceive: context not found[RETURN]")
            return
        }
        Log.i(TAG, "onBroadcastReceive: execution done")
    }

    @DelicateCoroutinesApi
    private fun startExecuting(context: Context, taskId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            Log.i(TAG, "onBroadcastReceive: taskId = $taskId")
            val mRepository = MyRepository.getInstance(context)
            mRepository.getTaskObject(taskId)?.let { mEntity ->
                val startTimeString = GPDateTime(mEntity.startTime).dateTimeString
                Log.i(TAG, "onBroadcastReceive: appId = ${mEntity.pkgName}")
                Log.i(TAG, "onBroadcastReceive: sTime = $startTimeString")
                withContext(Dispatchers.Main) {
                    openApp(context, mEntity.pkgName)
                }
                mRepository.markTaskAsCompleted(taskId)
            } ?: let {
                Toast.makeText(context, "app not launched", Toast.LENGTH_LONG).show()
                Log.e(TAG, "onBroadcastReceive: task object not found [RETURN]")
            }
        }
    }

    private fun openApp(context: Context, pkgName: String) {
        try {
            context.startActivity(
                context.packageManager?.getLaunchIntentForPackage(pkgName)?.apply {
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
    }
}