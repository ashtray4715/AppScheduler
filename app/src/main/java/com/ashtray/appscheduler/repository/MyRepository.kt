package com.ashtray.appscheduler.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.ashtray.appscheduler.common.GPAsyncExecutor
import com.ashtray.appscheduler.database.MyTaskDatabase
import com.ashtray.appscheduler.database.MyTaskEntity
import com.ashtray.appscheduler.common.GPLog.d

class MyRepository(context: Context) {

    companion object {
        private const val TAG = "MyRepository"
        private var INSTANCE: MyRepository? = null

        fun getInstance(context: Context): MyRepository {
            synchronized(this) {
                var currentInstance = INSTANCE
                if(currentInstance == null) {
                    currentInstance = MyRepository(context)
                    INSTANCE = currentInstance
                }
                return currentInstance
            }
        }
    }

    private val asyncExecutor = GPAsyncExecutor()
    private val myTaskDao = MyTaskDatabase.getInstance(context).getDao()
    private var completedTasks = myTaskDao.getAllTheCompletedTask()
    private var remainingTasks = myTaskDao.getAllTheRemainingTask()

    fun getRemainingTaskLiveData(): LiveData<List<MyTaskEntity>> = remainingTasks

    fun getCompletedTaskLiveData(): LiveData<List<MyTaskEntity>> = completedTasks

    fun getRemainingTaskList(): List<MyTaskEntity> {
        return myTaskDao.getRemainingTaskList()
    }

    fun insertNewTask(entity: MyTaskEntity) {
        asyncExecutor.executeAsync {
            d(TAG, "insertNewTask: executing.. taskId = ${entity.taskId}")
            myTaskDao.insertNewTask(entity)
        }
    }

    fun deleteMultipleTask(isDone: Boolean) {
        asyncExecutor.executeAsync {
            d(TAG, "deleteMultipleTask: executing.. isDone = $isDone")
            myTaskDao.deleteMultipleTask(isDone)
        }
    }

    fun deleteSingleTask(taskId: Int) {
        asyncExecutor.executeAsync {
            d(TAG, "deleteSingleTask: executing.. taskId = $taskId")
            myTaskDao.deleteSingleTask(taskId)
        }
    }

    fun markTaskAsCompleted(taskId: Int) {
        asyncExecutor.executeAsync {
            d(TAG, "markTaskAsCompleted: executing.. taskId = $taskId")
            myTaskDao.markTaskAsComplete(taskId)
        }
    }

    fun getTaskObject(taskId: Int): MyTaskEntity? {
        return myTaskDao.getTaskObject(taskId)
    }

}