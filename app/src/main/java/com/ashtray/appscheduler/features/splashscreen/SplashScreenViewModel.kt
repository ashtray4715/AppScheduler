package com.ashtray.appscheduler.features.splashscreen

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ashtray.appscheduler.database.MyTaskEntity
import com.ashtray.appscheduler.repository.MyRepository

@SuppressLint("CustomSplashScreen")
class SplashScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MyRepository.getInstance(application)

    fun getRemainingTaskListLiveData(): LiveData<List<MyTaskEntity>> {
        return repository.getRemainingTaskLiveData()
    }

    fun getCompletedTaskListLiveData(): LiveData<List<MyTaskEntity>> {
        return repository.getCompletedTaskLiveData()
    }
}