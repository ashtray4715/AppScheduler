package com.ashtray.appscheduler.repository

import androidx.lifecycle.LiveData
import com.ashtray.appscheduler.common.GPAsyncExecutor
import com.ashtray.appscheduler.database.MyTaskDatabase
import com.ashtray.appscheduler.database.MyTaskEntity
import com.ashtray.appscheduler.common.GPLog.d

class MyRepository {

    companion object {
        private const val TAG = "MyRepository"
        val INSTANCE = MyRepository()
    }

    private val asyncExecutor = GPAsyncExecutor()
    private val myTaskDao = MyTaskDatabase.getInstance().getDao()
    private var completedTasks = myTaskDao.getAllTheCompletedTask()
    private var remainingTasks = myTaskDao.getAllTheRemainingTask()

    fun getRemainingTaskLiveData(): LiveData<List<MyTaskEntity>> = remainingTasks

    fun getCompletedTaskLiveData(): LiveData<List<MyTaskEntity>> = completedTasks

    fun insertNewTask(entity: MyTaskEntity) {
        asyncExecutor.executeAsync {
            d(TAG, "insertNewTask: executing..")
            myTaskDao.insertNewTask(entity)
        }
    }

}