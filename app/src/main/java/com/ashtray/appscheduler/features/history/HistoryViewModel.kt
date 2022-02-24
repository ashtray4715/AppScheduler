package com.ashtray.appscheduler.features.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ashtray.appscheduler.database.MyTaskEntity
import com.ashtray.appscheduler.repository.MyRepository

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MyRepository.getInstance(application)

    fun getCompletedTaskListLiveData(): LiveData<List<MyTaskEntity>> {
        return repository.getCompletedTaskLiveData()
    }

    fun deleteMultipleTask(isDone: Boolean) {
        repository.deleteMultipleTask(isDone)
    }
}