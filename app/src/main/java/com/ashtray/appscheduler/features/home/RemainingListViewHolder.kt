package com.ashtray.appscheduler.features.home

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ashtray.appscheduler.R
import com.ashtray.appscheduler.common.GPDateTime
import com.ashtray.appscheduler.common.GPLog
import com.ashtray.appscheduler.common.GPUtils

class RemainingListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        private const val TAG = "RemainingListViewHolder"
    }

    fun displayAppInfo(context: Context?, taskInfo: RemainingTaskInfo, position: Int) {
        itemView.findViewById<TextView>(R.id.tv_app_name).text = taskInfo.appName
        itemView.findViewById<TextView>(R.id.tv_app_pck_name).text = taskInfo.appPkgName
        itemView.findViewById<TextView>(R.id.tv_start_time).text = taskInfo.startTime
        val iconView = itemView.findViewById<ImageView>(R.id.iv_app_icon)
        GPUtils().showAppIcon(iconView, taskInfo.appPkgName, context)

        val splitString = taskInfo.startTime.split(" ")
        val input1 = GPDateTime(splitString[1], splitString[0])
        GPLog.d(TAG, "displayAppInfo: 1 = [${input1.dateTimeString}]")
        GPLog.d(TAG, "displayAppInfo: 2 = [${input1.dateTimeLong}]")
        val input2 = GPDateTime(input1.dateTimeLong)
        GPLog.d(TAG, "displayAppInfo: 3 = [${input2.dateTimeString}]")
        GPLog.d(TAG, "displayAppInfo: 4 = [${input2.dateTimeLong}]")
    }
}