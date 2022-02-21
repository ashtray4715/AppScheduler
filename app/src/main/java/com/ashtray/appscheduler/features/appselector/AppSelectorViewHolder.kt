package com.ashtray.appscheduler.features.appselector

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ashtray.appscheduler.R

class AppSelectorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    interface CallBacks {
        fun onItemSelected(position: Int)
    }

    private var callBacks: CallBacks? = null
    private var currentPosition: Int = -1

    fun setCallBacks(callBacks: CallBacks) {
        this.callBacks = callBacks
    }

    init {
        itemView.setOnClickListener {
            callBacks?.onItemSelected(currentPosition)
        }
    }

    fun displayAppInfo(appInfo: MyAppInfo, position: Int) {
        currentPosition = position
        itemView.findViewById<TextView>(R.id.tv_app_name).text = appInfo.appName
        itemView.findViewById<TextView>(R.id.tv_app_pck_name).text = appInfo.appPkgName
        itemView.findViewById<ImageView>(R.id.iv_app_icon).setImageDrawable(appInfo.appIcon)
        itemView.findViewById<ImageView>(R.id.iv_selector).visibility = when(appInfo.isSelected) {
            true -> View.VISIBLE
            else -> View.INVISIBLE
        }
    }

}