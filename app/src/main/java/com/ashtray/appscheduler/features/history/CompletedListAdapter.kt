package com.ashtray.appscheduler.features.history

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ashtray.appscheduler.R

class CompletedListAdapter(
    private val context: Context?
): RecyclerView.Adapter<CompletedListViewHolder>() {

    private val completedTaskList = mutableListOf<CompletedTaskInfo>()

    @SuppressLint("NotifyDataSetChanged")
    fun setAppList(list: MutableList<CompletedTaskInfo>) {
        completedTaskList.clear()
        completedTaskList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedListViewHolder {
        return CompletedListViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_item_completed_task_info,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CompletedListViewHolder, position: Int) {
        holder.displayAppInfo(context, completedTaskList[position], position)
    }

    override fun getItemCount(): Int {
        return completedTaskList.size
    }
}