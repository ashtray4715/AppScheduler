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

    private fun checkIfItemInTheList(list: List<MyTaskEntity>, startTime: Long): Boolean {
        for(item in list) {
            if(item.startTime == startTime) {
                return true
            }
        }
        return false
    }

    fun checkIfItemIsNotInTheList(list: List<MyTaskEntity>, startTime: Long): Boolean {
        return !checkIfItemInTheList(list, startTime)
    }

    fun cancelSchedule(context: Context?, taskEntity: MyTaskEntity): Boolean {
        val gpDateTime1 = GPDateTime(taskEntity.startTime)
        d(TAG, "cancelSchedule1: $gpDateTime1")

        val gpDateTime2 = GPDateTime(gpDateTime1.dateString, gpDateTime1.timeString)
        d(TAG, "cancelSchedule2: $gpDateTime2")

        val gpDateTime3 = GPDateTime(gpDateTime2.dateTimeLong)
        d(TAG, "cancelSchedule3: $gpDateTime3")

        val broadCastId = taskEntity.startTime.toInt()
        d(TAG, "cancelSchedule: broadcast id value = $broadCastId")

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val intent = Intent(context, GPTaskExecutor::class.java).apply {
            putExtra(GPConst.PK_APP_ID, taskEntity.pkgName)
            putExtra(GPConst.PK_START_TIME, taskEntity.startTime.toString())
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            broadCastId,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager?.cancel(pendingIntent)
        return alarmManager != null
    }

    fun addSchedule(context: Context?, taskEntity: MyTaskEntity): Boolean {
        val gpDateTime1 = GPDateTime(taskEntity.startTime)
        d(TAG, "addSchedule1: $gpDateTime1")

        val gpDateTime2 = GPDateTime(gpDateTime1.dateString, gpDateTime1.timeString)
        d(TAG, "addSchedule2: $gpDateTime2")

        val gpDateTime3 = GPDateTime(gpDateTime2.dateTimeLong)
        d(TAG, "addSchedule3: $gpDateTime3")

        val broadCastId = taskEntity.startTime.toInt()
        d(TAG, "addSchedule: broadcast id value = $broadCastId")

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val intent = Intent(context, GPTaskExecutor::class.java).apply {
            putExtra(GPConst.PK_APP_ID, taskEntity.pkgName)
            putExtra(GPConst.PK_START_TIME, taskEntity.startTime.toString())
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            broadCastId,
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

}