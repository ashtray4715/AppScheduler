package com.ashtray.appscheduler.features.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ashtray.appscheduler.R

class RemainingListAdapter(
    private val context: Context?
): RecyclerView.Adapter<RemainingListViewHolder>() {

    private val remainingTaskList = mutableListOf<RemainingTaskInfo>()

    @SuppressLint("NotifyDataSetChanged")
    fun setAppList(list: MutableList<RemainingTaskInfo>) {
        remainingTaskList.clear()
        remainingTaskList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemainingListViewHolder {
        return RemainingListViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_item_remaining_task_info,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RemainingListViewHolder, position: Int) {
        holder.displayAppInfo(context, remainingTaskList[position], position)
    }

    override fun getItemCount(): Int {
        return remainingTaskList.size
    }
}