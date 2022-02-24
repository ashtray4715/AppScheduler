package com.ashtray.appscheduler.features.timeselector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import com.ashtray.appscheduler.R
import com.ashtray.appscheduler.common.GPConst
import com.ashtray.appscheduler.common.GPFragment
import com.ashtray.appscheduler.common.GPLog
import com.ashtray.appscheduler.databinding.FragmentTimeSelectorBinding

class TimeSelectorFragment: GPFragment() {

    companion object {
        private const val TAG = "TimeSelectorFragment"

        fun newInstance(): TimeSelectorFragment {
            val args = Bundle()
            val fragment = TimeSelectorFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getUniqueTag(): String {
        return TAG
    }

    private lateinit var viewModel: TimeSelectorViewModel
    private lateinit var binding: FragmentTimeSelectorBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        GPLog.d(TAG, "onCreateView: called")
        viewModel = ViewModelProvider(this).get(TimeSelectorViewModel::class.java)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_time_selector,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            actionBar.setBackListener { handleBackButtonPressed() }
            cancelButton.setOnClickListener { handleBackButtonPressed() }
            selectButton.setOnClickListener { timeSelectedBtnPressed() }
        }
    }

    private fun timeSelectedBtnPressed() {
        val timeValue = String.format(
            "%02d:%02d:01", binding.timePicker.hour, binding.timePicker.minute
        )
        setFragmentResult(GPConst.PK_TIME, bundleOf(GPConst.PK_TIME to  timeValue))
        changeFragment(this, TransactionType.REMOVE_FRAGMENT)
    }

    override fun handleBackButtonPressed(): Boolean {
        changeFragment(this, TransactionType.REMOVE_FRAGMENT)
        return true
    }

}