package com.ashtray.appscheduler.features.editschedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ashtray.appscheduler.database.MyTaskEntity
import com.ashtray.appscheduler.repository.MyRepository

class EditScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MyRepository.getInstance(application)

    fun getRemainingTaskListLiveData(): LiveData<List<MyTaskEntity>> {
        return repository.getRemainingTaskLiveData()
    }

    fun deleteSingleTask(taskId: Int) {
        repository.deleteSingleTask(taskId)
    }

    fun addNewTask(entity: MyTaskEntity) {
        repository.insertNewTask(entity)
    }

    fun getTaskObject(taskId: Int): MyTaskEntity? {
        return repository.getTaskObject(taskId)
    }
}