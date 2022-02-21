package com.ashtray.appscheduler.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class MyTaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "start_time")
    var startTime: String,

    @ColumnInfo(name = "app_name")
    val appName: String?,

    @ColumnInfo(name = "pkg_name")
    val pkgName: String?,

    @ColumnInfo(name = "is_done")
    val isDone: Boolean?
)




