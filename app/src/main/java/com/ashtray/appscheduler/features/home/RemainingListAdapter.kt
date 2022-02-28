package com.ashtray.appscheduler.features.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ashtray.appscheduler.R
import com.ashtray.appscheduler.common.GPLog.e
import java.lang.Exception

class RemainingListAdapter(
    private val context: Context?,
    private val callBacks: RemainingListViewHolder.CallBacks?
): RecyclerView.Adapter<RemainingListViewHolder>() {

    companion object {
        private const val TAG = "RemainingListAdapter"
    }

    private val remainingTaskList = mutableListOf<RemainingTaskInfo>()

    @SuppressLint("NotifyDataSetChanged")
    fun setAppList(list: MutableList<RemainingTaskInfo>) {
        remainingTaskList.clear()
        remainingTaskList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemainingListViewHolder {
        return RemainingListViewHolder(
            callBacks,
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

    fun getItemFromPosition(position: Int): RemainingTaskInfo? {
        return try {
            remainingTaskList[position]
        } catch (e: Exception) {
            e(TAG, "getItemFromPosition: error occurs")
            e.printStackTrace()
            null
        }
    }
}