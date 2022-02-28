package com.ashtray.appscheduler.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class MyTaskEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "task_id")
    var taskId: Int,

    @ColumnInfo(name = "start_time")
    var startTime: Long,

    @ColumnInfo(name = "app_name")
    val appName: String,

    @ColumnInfo(name = "pkg_name")
    val pkgName: String,

    @ColumnInfo(name = "is_done")
    val isDone: Boolean
)




