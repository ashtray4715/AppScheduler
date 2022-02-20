package com.ashtray.appscheduler.features.history

import com.ashtray.appscheduler.common.GPFragment

class HistoryFragment: GPFragment() {

    companion object {
        private const val TAG = "HistoryFragment"
    }

    override fun getUniqueTag(): String {
        return TAG
    }
}