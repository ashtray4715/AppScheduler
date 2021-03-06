package com.ashtray.appscheduler.common

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.ashtray.appscheduler.common.GPLog.e
import com.ashtray.appscheduler.common.GPLog.d
import com.ashtray.appscheduler.R
import com.ashtray.appscheduler.database.MyTaskEntity
import android.view.Window

class GPUtils {

    companion object {
        private const val TAG = "GPUtils"
    }

    fun getAppNameFromPkgName(context: Context?, packageName: String?): String {
        return try {
            packageName?.let {
                context?.packageManager?.getApplicationLabel(
                    context.packageManager.getApplicationInfo(
                        packageName,
                        PackageManager.GET_META_DATA
                    )
                ) as String? ?: GPConst.MSG_NO_APP_SELECTED
            } ?: GPConst.MSG_NO_APP_SELECTED
        } catch (e: Exception) {
            e(TAG, "getAppNameFromPkgName: problem occurs [${e.message}]")
            e.printStackTrace()
            GPConst.MSG_NO_APP_SELECTED
        }
    }

    fun showAppIcon(ivAppIcon: ImageView, pkgName: String?, context: Context?) {
        try {
            pkgName?.let {
                context?.packageManager?.getApplicationIcon(it)?.let { icon ->
                    ivAppIcon.setImageDrawable(icon)
                } ?: {
                    e(TAG, "showAppIcon: invalid package name")
                    ivAppIcon.setImageDrawable(
                        ContextCompat.getDrawable(context!!, R.drawable.ic_no_image_24)
                    )
                }
            }
        } catch (e: Exception) {
            e(TAG, "showAppIcon: problem occurs [${e.message}]")
            e.printStackTrace()
            context?.let {
                ivAppIcon.setImageDrawable(
                    ContextCompat.getDrawable(it, R.drawable.ic_no_image_24)
                )
            }
        }
    }

    private fun checkIfItemInTheList(list: List<MyTaskEntity>, cId: Int): Boolean {
        for(item in list) {
            if(item.taskId == cId) {
                return true
            }
        }
        return false
    }

    fun checkIfItemIsNotInTheList(list: List<MyTaskEntity>, cId: Int): Boolean {
        return !checkIfItemInTheList(list, cId)
    }

    fun cancelSchedule(context: Context?, taskEntity: MyTaskEntity): Boolean {
        d(TAG, "cancelSchedule: pkgName = ${taskEntity.pkgName}")
        val taskDateTime = GPDateTime(taskEntity.startTime).dateTimeString
        d(TAG, "cancelSchedule: time = $taskDateTime")
        d(TAG, "cancelSchedule: id value = ${taskEntity.taskId}")

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val intent = Intent(context, GPTaskExecutor::class.java).apply {
            putExtra(GPConst.PK_TASK_ID, taskEntity.taskId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskEntity.taskId,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager?.cancel(pendingIntent)
        return alarmManager != null
    }

    fun addSchedule(context: Context?, taskEntity: MyTaskEntity): Boolean {
        d(TAG, "addSchedule: pkgName = ${taskEntity.pkgName}")
        val taskDateTime = GPDateTime(taskEntity.startTime).dateTimeString
        d(TAG, "addSchedule: time = $taskDateTime")
        d(TAG, "addSchedule: id value = ${taskEntity.taskId}")

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val intent = Intent(context, GPTaskExecutor::class.java).apply {
            putExtra(GPConst.PK_TASK_ID, taskEntity.taskId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskEntity.taskId,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager?.cancel(pendingIntent)
        alarmManager?.setExact(
            AlarmManager.RTC_WAKEUP,
            taskEntity.startTime,
            pendingIntent
        )
        return alarmManager != null
    }

    fun setWindowBackground(context: Context?, window: Window?, drawableId: Int) {
        context?.let {
            val drawable = ContextCompat.getDrawable(it, drawableId)
            window?.setBackgroundDrawable(drawable)
        }
    }

}