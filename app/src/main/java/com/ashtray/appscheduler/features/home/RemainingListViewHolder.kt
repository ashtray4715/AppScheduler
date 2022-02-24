package com.ashtray.appscheduler.features.home

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ashtray.appscheduler.R
import com.ashtray.appscheduler.common.GPUtils

class RemainingListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun displayAppInfo(context: Context?, taskInfo: RemainingTaskInfo, position: Int) {
        itemView.findViewById<TextView>(R.id.tv_app_name).text = taskInfo.appName
        itemView.findViewById<TextView>(R.id.tv_app_pck_name).text = taskInfo.appPkgName
        itemView.findViewById<TextView>(R.id.tv_start_time).text = taskInfo.startTime
        val iconView = itemView.findViewById<ImageView>(R.id.iv_app_icon)
        GPUtils().showAppIcon(iconView, taskInfo.appPkgName, context)
    }
}