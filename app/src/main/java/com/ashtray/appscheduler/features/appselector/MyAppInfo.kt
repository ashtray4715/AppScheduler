package com.ashtray.appscheduler.features.appselector

import android.graphics.drawable.Drawable

data class MyAppInfo(
    val appName: String,
    val appPkgName: String,
    val appIcon: Drawable?,
    var isSelected: Boolean = false
)