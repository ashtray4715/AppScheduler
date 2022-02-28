package com.ashtray.appscheduler.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MyTaskDao {
    @Query("SELECT * FROM task_table WHERE is_done = 1 ORDER BY start_time ASC")
    fun getAllTheCompletedTask(): LiveData<List<MyTaskEntity>>

    @Query("SELECT * FROM task_table WHERE is_done = 0 ORDER BY start_time ASC")
    fun getAllTheRemainingTask(): LiveData<List<MyTaskEntity>>

    @Query("SELECT * FROM task_table WHERE is_done = 0 ORDER BY start_time ASC")
    fun getRemainingTaskList(): List<MyTaskEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertNewTask(entityMy: MyTaskEntity)

    @Query("DELETE FROM task_table WHERE start_time = :startTime")
    fun deleteSingleTask(startTime: Long)

    @Query("DELETE FROM task_table WHERE is_done = :isDone")
    fun deleteMultipleTask(isDone: Boolean)

    @Query("UPDATE task_table SET start_time = :nTime WHERE start_time = :pTime")
    fun updateExistingTask(pTime: Long, nTime: Long)

    @Query("UPDATE task_table SET is_done = 1 WHERE start_time = :startTime")
    fun markTaskAsComplete(startTime: Long)
}