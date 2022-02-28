package com.ashtray.appscheduler.common

import android.content.Context
import com.ashtray.appscheduler.common.GPLog.d

class GPSharedPref(val context: Context?) {

    companion object {
        private const val SHARED_PREF_KEY = "task_id_generator_shared_pref"
        private const val SHARED_PREF_VALUE_KEY = "task_id_generator_shared_pref_value"
        private const val TAG = "GPSharedPref"
    }

    fun getNewId(): Int {
        val sharedPref = context?.getSharedPreferences(
            SHARED_PREF_KEY,
            Context.MODE_PRIVATE
        )
        val currentValue = sharedPref?.getInt(SHARED_PREF_VALUE_KEY, 99) ?: 99
        val newValue = currentValue + 1
        sharedPref?.edit()?.putInt(SHARED_PREF_VALUE_KEY, newValue)?.apply()
        d(TAG, "getNewId: returnValue = $currentValue")
        return currentValue
    }
}