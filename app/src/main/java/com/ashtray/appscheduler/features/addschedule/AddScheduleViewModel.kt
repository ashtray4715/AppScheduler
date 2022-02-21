package com.ashtray.appscheduler.features.addschedule

import androidx.lifecycle.ViewModel
import com.ashtray.appscheduler.database.MyTaskEntity
import com.ashtray.appscheduler.repository.MyRepository

class AddScheduleViewModel: ViewModel() {
    private val repository = MyRepository.INSTANCE

    fun addNewSchedule(entity: MyTaskEntity): Boolean {
        val remainingList = repository.getRemainingTaskLiveData().value ?: emptyList()
        for(singleEntity in remainingList) {
            if(singleEntity.startTime == entity.startTime) {
                return false
            }
        }
        repository.insertNewTask(entity)
        return true
    }
}