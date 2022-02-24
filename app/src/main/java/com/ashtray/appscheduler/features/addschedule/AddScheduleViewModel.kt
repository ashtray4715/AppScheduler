package com.ashtray.appscheduler.features.addschedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ashtray.appscheduler.database.MyTaskEntity
import com.ashtray.appscheduler.repository.MyRepository

class AddScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MyRepository.getInstance(application)

    fun addNewSchedule(entity: MyTaskEntity) {
        repository.insertNewTask(entity)
    }

    fun getRemainingTaskListLiveData(): LiveData<List<MyTaskEntity>> {
        return repository.getRemainingTaskLiveData()
    }
}