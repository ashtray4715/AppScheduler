package com.ashtray.appscheduler.repository

import com.ashtray.appscheduler.database.MyTaskDatabase

class MyRepository() {

    companion object {
        private const val TAG = "MyRepository"
        val INSTANCE = MyRepository()
    }

    private val myTaskDao = MyTaskDatabase.getInstance().getDao()
    private var completedTasks = myTaskDao.getAllTheCompletedTask()
    private var remainingTasks = myTaskDao.getAllTheRemainingTask()

}