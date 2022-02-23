package com.ashtray.appscheduler.features.home

import com.ashtray.appscheduler.common.GPFragment
import com.ashtray.appscheduler.databinding.FragmentHomeBinding
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.databinding.DataBindingUtil

import androidx.lifecycle.ViewModelProvider

import android.view.ViewGroup

import android.view.LayoutInflater
import android.view.View

import com.ashtray.appscheduler.R
import com.ashtray.appscheduler.common.GPDateTime
import com.ashtray.appscheduler.common.GPLog.d
import com.ashtray.appscheduler.features.addschedule.AddScheduleFragment
import com.ashtray.appscheduler.features.history.HistoryFragment

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
        rAdapter = RemainingListAdapter(context)
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

}