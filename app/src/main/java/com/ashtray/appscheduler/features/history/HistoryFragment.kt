package com.ashtray.appscheduler.features.history

import android.os.Bundle
import com.ashtray.appscheduler.common.GPFragment

class HistoryFragment: GPFragment() {

    companion object {
        private const val TAG = "HistoryFragment"

        fun newInstance(): HistoryFragment {
            val args = Bundle()
            val fragment = HistoryFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getUniqueTag(): String {
        return TAG
    }
}