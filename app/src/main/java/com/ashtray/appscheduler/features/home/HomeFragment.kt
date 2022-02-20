package com.ashtray.appscheduler.features.home

import com.ashtray.appscheduler.common.GPFragment

class HomeFragment: GPFragment() {

    companion object {
        private const val TAG = "HomeFragment"
    }

    override fun getUniqueTag(): String {
        return TAG
    }
}