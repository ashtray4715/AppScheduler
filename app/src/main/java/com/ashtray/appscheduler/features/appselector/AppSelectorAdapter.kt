package com.ashtray.appscheduler.features.appselector

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ashtray.appscheduler.R

class AppSelectorAdapter(
    private val context: Context?
): RecyclerView.Adapter<AppSelectorViewHolder>() {

    private val appList = mutableListOf<MyAppInfo>()

    @SuppressLint("NotifyDataSetChanged")
    fun setAppList(list: MutableList<MyAppInfo>) {
        appList.clear()
        appList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppSelectorViewHolder {
        return AppSelectorViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_item_app_details,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AppSelectorViewHolder, position: Int) {
        holder.displayAppInfo(appList[position], position)
    }

    override fun getItemCount(): Int {
        return appList.size
    }

}