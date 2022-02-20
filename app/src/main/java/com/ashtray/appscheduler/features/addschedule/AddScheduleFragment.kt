package com.ashtray.appscheduler.features.addschedule

import android.os.Bundle
import com.ashtray.appscheduler.common.GPFragment

class AddScheduleFragment: GPFragment() {

    companion object {
        private const val TAG = "AddScheduleFragment"

        fun newInstance(): AddScheduleFragment {
            val args = Bundle()
            val fragment = AddScheduleFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getUniqueTag(): String {
        return TAG
    }
}