package com.ashtray.appscheduler.features.addschedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ashtray.appscheduler.database.MyTaskEntity
import com.ashtray.appscheduler.repository.MyRepository

class AddScheduleViewModel: ViewModel() {
    private val repository = MyRepository.INSTANCE

    fun addNewSchedule(entity: MyTaskEntity) {
        repository.insertNewTask(entity)
    }

    fun getRemainingTaskListLiveData(): LiveData<List<MyTaskEntity>> {
        return repository.getRemainingTaskLiveData()
    }
}