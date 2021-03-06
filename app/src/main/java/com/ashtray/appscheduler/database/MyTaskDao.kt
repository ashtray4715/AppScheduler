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

    @Query("SELECT * FROM task_table WHERE task_id = :taskId")
    fun getTaskObject(taskId: Int): MyTaskEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertNewTask(entityMy: MyTaskEntity)

    @Query("DELETE FROM task_table WHERE task_id = :taskId")
    fun deleteSingleTask(taskId: Int)

    @Query("DELETE FROM task_table WHERE is_done = :isDone")
    fun deleteMultipleTask(isDone: Boolean)

    @Query("UPDATE task_table SET is_done = 1 WHERE task_id = :taskId")
    fun markTaskAsComplete(taskId: Int)
}