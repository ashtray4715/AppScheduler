package com.ashtray.appscheduler.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy

@Dao
interface MyTaskDao {
    @Query("SELECT * FROM task_table WHERE is_done = 1")
    fun getAllTheCompletedTask(): LiveData<List<MyTaskEntity>>

    @Query("SELECT * FROM task_table WHERE is_done = 0")
    fun getAllTheRemainingTask(): LiveData<List<MyTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertNewTask(entityMy: MyTaskEntity): Boolean

    @Query("DELETE FROM task_table WHERE start_time = :startTime")
    fun deleteGame(startTime: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateExistingTask(entityMy: MyTaskEntity)
}