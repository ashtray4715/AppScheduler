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
import com.ashtray.appscheduler.common.GPLog.e
import com.ashtray.appscheduler.database.MyTaskEntity
import com.ashtray.appscheduler.databinding.FragmentEditScheduleBinding
import com.ashtray.appscheduler.features.dateselector.DateSelectorFragment
import com.ashtray.appscheduler.features.splashscreen.SplashScreenFragment
import com.ashtray.appscheduler.features.timeselector.TimeSelectorFragment
import java.text.SimpleDateFormat
import java.util.*

class EditScheduleFragment : GPFragment() {
    companion object {
        private const val TAG = "EditScheduleFragment"

        fun newInstance(taskId: Int): EditScheduleFragment {
            val args = Bundle()
            args.putInt(GPConst.PK_TASK_ID, taskId)
            val fragment = EditScheduleFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getUniqueTag(): String = TAG

    private lateinit var viewModel: EditScheduleViewModel
    private lateinit var binding: FragmentEditScheduleBinding
    private lateinit var selectedTask: MyTaskEntity

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
        initializeSelectedTask()
        drawPreviouslySelectedTaskInfo()
        setFragmentResultListener(GPConst.PK_DATE) { key, bundle ->
            dateGotSelected(bundle.getString(key))
        }
        setFragmentResultListener(GPConst.PK_TIME) { key, bundle ->
            timeGotSelected(bundle.getString(key))
        }
        return binding.root
    }

    private fun initializeSelectedTask() {
        val taskId = arguments?.getInt(GPConst.PK_TASK_ID, -1) ?: let {
            e(TAG, "drawPreviouslySelectedTaskInfo: task id not found")
            return
        }
        viewModel.getTaskObject(taskId)?.let { currentTask ->
            selectedTask = currentTask
        }
    }

    private fun drawPreviouslySelectedTaskInfo() {
        try {
            val gpDateTime = GPDateTime(selectedTask.startTime)
            binding.apply {
                tvAppName.text = selectedTask.appName
                tvAppPckName.text = selectedTask.pkgName
                tvTimeValue.text = gpDateTime.timeString
                tvDateValue.text = gpDateTime.dateString
                GPUtils().showAppIcon(ivAppIcon, selectedTask.pkgName, context)
            }
        } catch (e: Exception) {
            showToastMessage("selected task not found")
            e(TAG, "drawPreviouslySelectedTaskInfo: selected task not found")
            e.printStackTrace()
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
            if(GPUtils().checkIfItemIsNotInTheList(list, selectedTask.taskId)) {
                showToastMessage("Task state changed")
                changeFragment(
                    SplashScreenFragment.newInstance(),
                    TransactionType.CLEAR_ALL_AND_ADD_NEW_FRAGMENT
                )
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
            e(TAG, "saveButtonPressed: past time selection error")
            return
        }
        if(selectedTimeSlotAlreadyBooked(selectedDateTime)) {
            showToastMessage("Time slot already booked")
            e(TAG, "saveButtonPressed: time slot already booked error")
            return
        }
        val newTask = MyTaskEntity(
            taskId = GPSharedPref(context).getNewId(),
            startTime = selectedDateTime,
            appName = appName,
            pkgName = appPkgName,
            isDone = false
        )

        val cancelStatus = GPUtils().cancelSchedule(context, selectedTask)
        d(TAG, "saveButtonPressed: cancel status = $cancelStatus")
        viewModel.deleteSingleTask(selectedTask.taskId)

        val insertStatus = GPUtils().addSchedule(context, newTask)
        d(TAG, "saveButtonPressed: insert status = $insertStatus")
        viewModel.addNewTask(newTask)

        showToastMessage("Task updated")
        changeFragment(this, TransactionType.REMOVE_FRAGMENT)
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
            e(TAG, "getSelectedDate: date string parsing error")
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
            e(TAG, "getUserSelectedTime: time string parsing error")
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