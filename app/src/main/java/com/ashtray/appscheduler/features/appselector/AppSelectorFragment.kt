package com.ashtray.appscheduler.features.appselector

import android.os.Bundle
import com.ashtray.appscheduler.common.GPFragment

class AppSelectorFragment: GPFragment() {

    companion object {
        private const val TAG = "AppSelectorFragment"

        fun newInstance(): AppSelectorFragment {
            val args = Bundle()
            val fragment = AppSelectorFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getUniqueTag(): String {
        return TAG
    }
}