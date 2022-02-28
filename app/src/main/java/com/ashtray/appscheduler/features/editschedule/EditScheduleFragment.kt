package com.ashtray.appscheduler.features.editschedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.ashtray.appscheduler.R
import com.ashtray.appscheduler.common.*
import com.ashtray.appscheduler.common.GPLog.d
import com.ashtray.appscheduler.database.MyTaskEntity
import com.ashtray.appscheduler.databinding.FragmentEditScheduleBinding
import com.ashtray.appscheduler.features.dateselector.DateSelectorFragment
import com.ashtray.appscheduler.features.timeselector.TimeSelectorFragment
import java.text.SimpleDateFormat
import java.util.*

class EditScheduleFragment : GPFragment() {
    companion object {
        private const val TAG = "EditScheduleFragment"

        fun newInstance(appPkgName: String, startTime: String): EditScheduleFragment {
            val args = Bundle()
            args.putString(GPConst.PK_APP_ID, appPkgName)
            args.putString(GPConst.PK_START_TIME, startTime)
            val fragment = EditScheduleFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getUniqueTag(): String = TAG

    private lateinit var viewModel: EditScheduleViewModel
    private lateinit var binding: FragmentEditScheduleBinding
    private var selectedTaskStartTime: Long = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        d(TAG, "onCreateView: called")
        viewModel = ViewModelProvider(this).get(EditScheduleViewModel::class.java)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_edit_schedule,
            container,
            false
        )
        drawPreviouslySelectedTaskInfo()
        setFragmentResultListener(GPConst.PK_DATE) { key, bundle ->
            dateGotSelected(bundle.getString(key))
        }
        setFragmentResultListener(GPConst.PK_TIME) { key, bundle ->
            timeGotSelected(bundle.getString(key))
        }
        return binding.root
    }

    private fun drawPreviouslySelectedTaskInfo() {
        arguments?.let { arg ->
            arg.getString(GPConst.PK_APP_ID)?.let { appId ->
                d(TAG, "drawPreviouslySelectedTaskInfo: $appId")
                val appName = GPUtils().getAppNameFromPkgName(context, appId)
                binding.tvAppName.text = appName
                binding.tvAppPckName.text = appId
                GPUtils().showAppIcon(binding.ivAppIcon, appId, context)
            }
            arg.getString(GPConst.PK_START_TIME)?.let { startTime ->
                d(TAG, "drawPreviouslySelectedTaskInfo: $startTime")
                val combinedTime = startTime.split(" ")
                binding.tvTimeValue.text = combinedTime[0]
                binding.tvDateValue.text = combinedTime[1]
                selectedTaskStartTime = GPDateTime(
                    combinedTime[1],
                    combinedTime[0]
                ).dateTimeLong
            }
        }
        if(selectedTaskStartTime == -1L) {
            showToastMessage("selected time parsing error")
            changeFragment(this, TransactionType.REMOVE_FRAGMENT)
            return
        }
        getUserSelectedAppName() ?: let {
            showToastMessage("app name parsing error")
            changeFragment(this, TransactionType.REMOVE_FRAGMENT)
            return
        }
        getUserSelectedAppPkgName() ?: let {
            showToastMessage("package name parsing error")
            changeFragment(this, TransactionType.REMOVE_FRAGMENT)
            return
        }
        getUserSelectedDate() ?: let {
            showToastMessage("date parsing error")
            changeFragment(this, TransactionType.REMOVE_FRAGMENT)
            return
        }
        getUserSelectedTime() ?: let {
            showToastMessage("time parsing error")
            changeFragment(this, TransactionType.REMOVE_FRAGMENT)
            return
        }
    }

    override fun onDestroyView() {
        clearFragmentResultListener(GPConst.PK_DATE)
        clearFragmentResultListener(GPConst.PK_TIME)
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            ivChangeDate.setOnClickListener { changeDateClicked() }
            ivChangeTime.setOnClickListener { changeTimeClicked() }
            actionBar.setBackListener { handleBackButtonPressed() }
            actionBar.setMenuListener1 { saveButtonPressed() }
            saveButton.setOnClickListener { saveButtonPressed() }
            cancelButton.setOnClickListener { handleBackButtonPressed() }
        }
        viewModel.getRemainingTaskListLiveData().observe(viewLifecycleOwner, { list ->
            if(GPUtils().checkIfItemIsNotInTheList(list, selectedTaskStartTime)) {
                showToastMessage("Task state changed")
                changeFragment(this, TransactionType.REMOVE_FRAGMENT)
            }
        })
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

    private fun saveButtonPressed() {
        val appName = getUserSelectedAppName() ?: let {
            showToastMessage("Select app first")
            return
        }
        val appPkgName = getUserSelectedAppPkgName() ?: let {
            showToastMessage("Select app first")
            return
        }
        val startDate = getUserSelectedDate() ?: let {
            showToastMessage("Select date first")
            return
        }
        val startTime = getUserSelectedTime() ?: let {
            showToastMessage("Select time first")
            return
        }
        val selectedDateTime = GPDateTime(startDate, startTime).dateTimeLong
        if(System.currentTimeMillis() > selectedDateTime) {
            showToastMessage("Select future date")
            GPLog.e(TAG, "saveButtonPressed: past time selection error")
            return
        }
        if(selectedTimeSlotAlreadyBooked(selectedDateTime)) {
            showToastMessage("Time slot already booked")
            GPLog.e(TAG, "saveButtonPressed: time slot already booked error")
            return
        }
        val newTask = MyTaskEntity(
            startTime = selectedDateTime,
            appName = appName,
            pkgName = appPkgName,
            isDone = false
        )
        val previousTask = MyTaskEntity(
            startTime = selectedTaskStartTime,
            appName = appName,
            pkgName = appPkgName,
            isDone = false
        )
        if(GPUtils().cancelSchedule(context, previousTask) and
            GPUtils().addSchedule(context, newTask)) {
            viewModel.updateExistingTask(
                selectedTaskStartTime,
                selectedDateTime
            )
            showToastMessage("Task updated")
            changeFragment(this, TransactionType.REMOVE_FRAGMENT)
        } else {
            showToastMessage("Update failed")
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

    private fun getUserSelectedDate(): String? {
        val startDate = binding.tvDateValue.text.toString()
        return try {
            SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(startDate)
            startDate
        } catch (e: Exception) {
            GPLog.e(TAG, "getSelectedDate: date string parsing error")
            e.printStackTrace()
            null
        }
    }

    private fun getUserSelectedTime(): String? {
        val startTime = binding.tvTimeValue.text.toString()
        return try {
            SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).parse(startTime)
            startTime
        } catch (e: Exception) {
            GPLog.e(TAG, "getUserSelectedTime: time string parsing error")
            e.printStackTrace()
            null
        }
    }

    private fun getUserSelectedAppName(): String? {
        val name = binding.tvAppName.text.toString()
        return when(name.isNotEmpty()) {
            true -> name
            else -> null
        }
    }

    private fun getUserSelectedAppPkgName(): String? {
        val pkgName = binding.tvAppPckName.text.toString()
        return when(pkgName.isNotEmpty()) {
            true -> pkgName
            else -> null
        }
    }

    private fun selectedTimeSlotAlreadyBooked(selectedDateTime: Long): Boolean {
        for(item in viewModel.getRemainingTaskListLiveData().value ?: emptyList()) {
            if(item.startTime == selectedDateTime) {
                return true
            }
        }
        return false
    }
}