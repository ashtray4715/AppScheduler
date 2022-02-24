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
import com.ashtray.appscheduler.common.GPLog.d
import com.ashtray.appscheduler.common.GPLog.e
import com.ashtray.appscheduler.databinding.FragmentAddScheduleBinding
import com.ashtray.appscheduler.features.appselector.AppSelectorFragment
import com.ashtray.appscheduler.features.dateselector.DateSelectorFragment
import com.ashtray.appscheduler.features.timeselector.TimeSelectorFragment

import com.ashtray.appscheduler.database.MyTaskEntity
import java.text.SimpleDateFormat
import java.util.*
import android.app.AlarmManager

import android.app.PendingIntent
import android.content.Context

import android.content.Intent
import com.ashtray.appscheduler.common.*


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
        val myNewTask = MyTaskEntity(
            startTime = selectedDateTime,
            appName = appName,
            pkgName = appPkgName,
            isDone = false
        )
        if(System.currentTimeMillis() > selectedDateTime) {
            showToastMessage("Select future date")
            e(TAG, "saveButtonPressed: past time selection error")
            return
        }
        if(selectionTimeSlotAlreadyBooked(selectedDateTime)) {
            showToastMessage("Time slot already booked")
            e(TAG, "saveButtonPressed: time slot already booked error")
            return
        }
        if(scheduleTask(myNewTask)) {
            viewModel.addNewSchedule(myNewTask)
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

    private fun scheduleTask(myTaskEntity: MyTaskEntity): Boolean {
        val gpDateTime1 = GPDateTime(myTaskEntity.startTime)
        d(TAG, "scheduleTask1: $gpDateTime1")

        val gpDateTime2 = GPDateTime(gpDateTime1.dateString, gpDateTime1.timeString)
        d(TAG, "scheduleTask2: $gpDateTime2")

        val gpDateTime3 = GPDateTime(gpDateTime2.dateTimeLong)
        d(TAG, "scheduleTask3: $gpDateTime3")



        d(TAG, "scheduleTask: broadcast id value = ${myTaskEntity.startTime.toInt()}")

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        val intent = Intent(context, GPTaskExecutor::class.java).apply {
            putExtra(GPConst.PK_APP_ID, myTaskEntity.pkgName)
            putExtra(GPConst.PK_START_TIME, myTaskEntity.startTime.toString())
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            myTaskEntity.startTime.toInt(),
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager?.set(
            AlarmManager.RTC_WAKEUP,
            myTaskEntity.startTime,
            pendingIntent
        )
        return alarmManager != null
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
            e(TAG, "saveButtonPressed: time string parsing error")
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

    private fun selectionTimeSlotAlreadyBooked(selectedDateTime: Long): Boolean {
        for(item in viewModel.getRemainingTaskListLiveData().value ?: emptyList()) {
            if(item.startTime == selectedDateTime) {
                return true
            }
        }
        return false
    }
}