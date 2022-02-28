package com.ashtray.appscheduler.features.home

data class RemainingTaskInfo(
    val taskId: Int,
    val appName: String,
    val appPkgName: String,
    val startTime: String
)