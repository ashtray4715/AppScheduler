package com.ashtray.appscheduler.features.appselector

import com.ashtray.appscheduler.common.GPFragment

class AppSelectorFragment: GPFragment() {

    companion object {
        private const val TAG = "AppSelectorFragment"
    }

    override fun getUniqueTag(): String {
        return TAG
    }
}