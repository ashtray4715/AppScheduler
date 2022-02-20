package com.ashtray.appscheduler.features.addschedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.ashtray.appscheduler.R
import com.ashtray.appscheduler.common.GPConst
import com.ashtray.appscheduler.common.GPFragment
import com.ashtray.appscheduler.common.GPLog.d
import com.ashtray.appscheduler.databinding.FragmentAddScheduleBinding

class AddScheduleFragment: GPFragment() {

    companion object {
        private const val TAG = "AddScheduleFragment"

        fun newInstance(): AddScheduleFragment {
            val args = Bundle()
            val fragment = AddScheduleFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getUniqueTag(): String {
        return TAG
    }

    private lateinit var viewModel: AddScheduleViewModel
    private lateinit var binding: FragmentAddScheduleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        d(TAG, "onCreateView: called")
        viewModel = ViewModelProvider(this).get(AddScheduleViewModel::class.java)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_schedule,
            container,
            false
        )
        setFragmentResultListener(GPConst.PK_APP_ID) { key, bundle ->
            appGotSelected(bundle.getString(key))
        }
        setFragmentResultListener(GPConst.PK_DATE) { key, bundle ->
            dateGotSelected(bundle.getString(key))
        }
        setFragmentResultListener(GPConst.PK_TIME) { key, bundle ->
            timeGotSelected(bundle.getString(key))
        }
        return binding.root
    }

    override fun onDestroyView() {
        clearFragmentResultListener(GPConst.PK_APP_ID)
        clearFragmentResultListener(GPConst.PK_DATE)
        clearFragmentResultListener(GPConst.PK_TIME)
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            ivChangeApp.setOnClickListener { changeAppClicked() }
            ivChangeDate.setOnClickListener { changeDateClicked() }
            ivChangeTime.setOnClickListener { changeTimeClicked() }
            actionBar.setBackListener { handleBackButtonPressed() }
        }
    }

    override fun handleBackButtonPressed(): Boolean {
        changeFragment(this, TransactionType.REMOVE_FRAGMENT)
        return true
    }

    private fun changeTimeClicked() {
        d(TAG, "changeTimeClicked: called")
    }

    private fun changeDateClicked() {
        d(TAG, "changeDateClicked: called")
    }

    private fun changeAppClicked() {
        d(TAG, "changeAppClicked: called")
    }

    private fun appGotSelected(appId: String?) {
        d(TAG, "appGotSelected: [appId=${appId}]")

    }

    private fun dateGotSelected(appId: String?) {
        d(TAG, "dateGotSelected: [appId=${appId}]")

    }

    private fun timeGotSelected(appId: String?) {
        d(TAG, "timeGotSelected: [appId=${appId}]")

    }
}