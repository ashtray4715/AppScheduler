package com.ashtray.appscheduler.features.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ashtray.appscheduler.database.MyTaskEntity
import com.ashtray.appscheduler.repository.MyRepository

class HomeViewModel: ViewModel() {

    private val repository = MyRepository.INSTANCE

    fun getRemainingTaskListLiveData(): LiveData<List<MyTaskEntity>> {
        return repository.getRemainingTaskLiveData()
    }

}