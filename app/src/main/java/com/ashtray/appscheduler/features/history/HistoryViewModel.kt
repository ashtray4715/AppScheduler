package com.ashtray.appscheduler.features.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ashtray.appscheduler.database.MyTaskEntity
import com.ashtray.appscheduler.repository.MyRepository

class HistoryViewModel: ViewModel() {
    private val repository = MyRepository.INSTANCE

    fun getCompletedTaskListLiveData(): LiveData<List<MyTaskEntity>> {
        return repository.getCompletedTaskLiveData()
    }

    fun deleteMultipleTask(isDone: Boolean) {
        repository.deleteMultipleTask(isDone)
    }
}