package com.ashtray.appscheduler.features.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashtray.appscheduler.R
import com.ashtray.appscheduler.common.GPDateTime
import com.ashtray.appscheduler.common.GPFragment
import com.ashtray.appscheduler.common.GPLog
import com.ashtray.appscheduler.databinding.FragmentHistoryBinding
import com.ashtray.appscheduler.features.addschedule.AddScheduleFragment

class HistoryFragment: GPFragment() {

    companion object {
        private const val TAG = "HistoryFragment"

        fun newInstance(): HistoryFragment {
            val args = Bundle()
            val fragment = HistoryFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: HistoryViewModel
    private lateinit var binding: FragmentHistoryBinding

    private lateinit var rAdapter: CompletedListAdapter

    override fun getUniqueTag(): String {
        return TAG
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        GPLog.d(TAG, "onCreateView: called")
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
        rAdapter = CompletedListAdapter(context)
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
            GPLog.d(TAG, "menuClickListener: delete all completed task")
            changeFragment(AddScheduleFragment.newInstance(), TransactionType.ADD_FRAGMENT)
        }
        binding.actionBar.setBackListener {
            GPLog.d(TAG, "backBtnPressed: close history fragment")
            changeFragment(this, TransactionType.REMOVE_FRAGMENT)
        }
        viewModel.getCompletedTaskListLiveData().observe(viewLifecycleOwner, { list ->
            val completedTaskList = mutableListOf<CompletedTaskInfo>()
            for(item in list) {
                completedTaskList.add(
                    CompletedTaskInfo(
                        appName = item.appName,
                        appPkgName = item.pkgName,
                        startTime = GPDateTime(item.startTime).dateTimeString
                    )
                )
            }
            rAdapter.setAppList(completedTaskList)
            binding.emptyListTextView.visibility = when(completedTaskList.isEmpty()) {
                true -> View.VISIBLE
                else -> View.GONE
            }
        })
    }
}