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
import com.ashtray.appscheduler.common.GPDateTime
import com.ashtray.appscheduler.common.GPFragment
import com.ashtray.appscheduler.common.GPLog.d
import com.ashtray.appscheduler.common.GPLog.e
import com.ashtray.appscheduler.databinding.FragmentAddScheduleBinding
import com.ashtray.appscheduler.features.appselector.AppSelectorFragment
import com.ashtray.appscheduler.features.dateselector.DateSelectorFragment
import com.ashtray.appscheduler.features.timeselector.TimeSelectorFragment

import com.ashtray.appscheduler.common.GPUtils
import com.ashtray.appscheduler.database.MyTaskEntity
import java.text.SimpleDateFormat
import java.util.*

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
            actionBar.setMenuListener1 { saveButtonPressed() }
            saveButton.setOnClickListener { saveButtonPressed() }
            cancelButton.setOnClickListener { handleBackButtonPressed() }
        }
    }

    private fun saveButtonPressed() {
        //app selection check
        if(binding.tvAppName.text.isEmpty() or binding.tvAppPckName.text.isEmpty()) {
            e(TAG, "saveButtonPressed: app not selected")
            showToastMessage("Select app first")
            return
        }

        //date selection check
        val startDate = binding.tvDateValue.text.toString()
        try {
            SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(startDate)
        } catch (e: Exception) {
            e(TAG, "saveButtonPressed: date string parsing error")
            e.printStackTrace()
            showToastMessage("Select date first")
            return
        }

        //time selection check
        val startTime = binding.tvTimeValue.text.toString()
        try {
            SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(startTime)
        } catch (e: Exception) {
            e(TAG, "saveButtonPressed: time string parsing error")
            e.printStackTrace()
            showToastMessage("Select time first")
            return
        }

        val myNewTask = MyTaskEntity(
            GPDateTime(startDate, startTime).dateTimeLong,
            binding.tvAppName.text.toString(),
            binding.tvAppPckName.text.toString(),
            false
        )
        if(viewModel.addNewSchedule(myNewTask)) {
            showToastMessage("Insertion done")
            changeFragment(this, TransactionType.REMOVE_FRAGMENT)
        } else {
            showToastMessage("Insertion failed")
        }
    }

    override fun handleBackButtonPressed(): Boolean {
        changeFragment(this, TransactionType.REMOVE_FRAGMENT)
        return true
    }

    private fun changeTimeClicked() {
        d(TAG, "changeTimeClicked: called")
        changeFragment(TimeSelectorFragment.newInstance(), TransactionType.ADD_FRAGMENT)
    }

    private fun changeDateClicked() {
        d(TAG, "changeDateClicked: called")
        changeFragment(DateSelectorFragment.newInstance(), TransactionType.ADD_FRAGMENT)
    }

    private fun changeAppClicked() {
        d(TAG, "changeAppClicked: called")
        changeFragment(AppSelectorFragment.newInstance(), TransactionType.ADD_FRAGMENT)
    }

    private fun appGotSelected(appId: String?) {
        d(TAG, "appGotSelected: [appId=${appId}]")
        binding.apply {
            tvAppName.text = GPUtils().getAppNameFromPkgName(context, appId)
            tvAppPckName.text = appId
            GPUtils().showAppIcon(ivAppIcon, appId, context)
        }
    }

    private fun dateGotSelected(dateValue: String?) {
        d(TAG, "dateGotSelected: [date=${dateValue}]")
        binding.tvDateValue.text = dateValue
    }

    private fun timeGotSelected(timeValue: String?) {
        d(TAG, "timeGotSelected: [time=${timeValue}]")
        binding.tvTimeValue.text = timeValue

    }
}