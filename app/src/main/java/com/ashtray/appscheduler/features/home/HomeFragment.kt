package com.ashtray.appscheduler.features.home

import android.annotation.SuppressLint
import android.app.Dialog
import com.ashtray.appscheduler.common.GPFragment
import com.ashtray.appscheduler.databinding.FragmentHomeBinding
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.databinding.DataBindingUtil

import androidx.lifecycle.ViewModelProvider

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT

import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.lifecycle.lifecycleScope

import com.ashtray.appscheduler.R
import com.ashtray.appscheduler.common.GPDateTime
import com.ashtray.appscheduler.common.GPLog.d
import com.ashtray.appscheduler.common.GPLog.e
import com.ashtray.appscheduler.common.GPUtils
import com.ashtray.appscheduler.features.addschedule.AddScheduleFragment
import com.ashtray.appscheduler.features.editschedule.EditScheduleFragment
import com.ashtray.appscheduler.features.history.HistoryFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment: GPFragment() {

    companion object {
        private const val TAG = "HomeFragment"

        fun newInstance(): HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    private lateinit var rAdapter: RemainingListAdapter

    private val viewHolderCallBack = object : RemainingListViewHolder.CallBacks {
        override fun onItemPressed(position: Int) {
            d(TAG, "viewHolderCallBack: onItemPressed called p=$position")
            rAdapter.getItemFromPosition(position)?.let {
                changeFragment(
                    EditScheduleFragment.newInstance(it.taskId),
                    TransactionType.ADD_FRAGMENT
                )
            } ?: let {
                showToastMessage("Restart app")
                e(TAG, "onItemPressed: no item found in adapter $position")
            }
        }

        override fun onItemLongPressed(position: Int) {
            d(TAG, "viewHolderCallBack: onItemLongPressed called p=$position")
            rAdapter.getItemFromPosition(position)?.let {
                showDeletePopUp(it.taskId)
            } ?: let {
                showToastMessage("Restart app")
                e(TAG, "onItemLongPressed: no item found in adapter $position")
            }
        }
    }

    override fun getUniqueTag(): String {
        return TAG
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        d(TAG, "onCreateView: called")
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        rAdapter = RemainingListAdapter(context, viewHolderCallBack)
        binding.rvAppList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rAdapter
        }
        binding.emptyListTextView.visibility = View.VISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.actionBar.setMenuListener1 {
            d(TAG, "menuClickListener: add new app schedule")
            changeFragment(AddScheduleFragment.newInstance(), TransactionType.ADD_FRAGMENT)
        }
        binding.actionBar.setBackListener {
            d(TAG, "backBtnPressed: open history fragment")
            changeFragment(HistoryFragment.newInstance(), TransactionType.ADD_FRAGMENT)
        }
        viewModel.getRemainingTaskListLiveData().observe(viewLifecycleOwner, { list ->
            val remainingTaskList = mutableListOf<RemainingTaskInfo>()
            for(item in list) {
                remainingTaskList.add(
                    RemainingTaskInfo(
                        taskId = item.taskId,
                        appName = item.appName,
                        appPkgName = item.pkgName,
                        startTime = GPDateTime(item.startTime).dateTimeString
                    )
                )
            }
            rAdapter.setAppList(remainingTaskList)
            binding.emptyListTextView.visibility = when(remainingTaskList.isEmpty()) {
                true -> View.VISIBLE
                else -> View.GONE
            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun showDeletePopUp(taskId: Int) {
        d(TAG, "showDeletePopUp: called for $taskId")
        val cDialog = Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_simple_confirmation)
            setCancelable(false)
            window?.setLayout(MATCH_PARENT, WRAP_CONTENT)
            window?.attributes?.windowAnimations = R.style.DialogScaleAnimation
        }

        GPUtils().setWindowBackground(
            context = context,
            window = cDialog.window,
            drawableId = R.drawable.common_dialog_background
        )

        val titleTv = cDialog.findViewById<TextView>(R.id.dialog_confirmation_title)
        val detailsTv = cDialog.findViewById<TextView>(R.id.dialog_confirmation_details)
        val okBtnTv = cDialog.findViewById<TextView>(R.id.dialog_confirmation_ok_button)
        val cancelBtnTv = cDialog.findViewById<TextView>(R.id.dialog_confirmation_cancel_button)

        titleTv.text = "Delete schedule"
        detailsTv.text = "Are you sure that you want to delete the schedule?"
        okBtnTv.text = "OK"
        cancelBtnTv.text = "Cancel"

        okBtnTv.setOnClickListener {
            d(TAG, "showDeletePopUp: ok pressed")
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.getTaskObject(taskId)?.let {
                    val cancelStatus = GPUtils().cancelSchedule(context, it)
                    d(TAG, "showDeletePopUp: cancel status = $cancelStatus")
                    viewModel.deleteSingleTask(taskId)
                    showToastMessage("Delete successful")
                } ?: let {
                    d(TAG, "showDeletePopUp: delete failed")
                    showToastMessage("Delete failed")
                }

            }

            cDialog.dismiss()
        }
        cancelBtnTv.setOnClickListener {
            d(TAG, "showDeletePopUp: cancel pressed")
            cDialog.dismiss()
        }

        cDialog.show()
    }

}