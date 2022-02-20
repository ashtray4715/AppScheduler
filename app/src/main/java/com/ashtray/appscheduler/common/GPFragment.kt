package com.ashtray.appscheduler.common

import android.content.Context
import androidx.fragment.app.Fragment

import java.lang.RuntimeException

abstract class GPFragment : Fragment() {

    interface CallBacks {
        fun changeFragment(fragment: GPFragment, transactionType: TransactionType)
        fun showToastMessage(message: String?)
    }

    enum class TransactionType {
        ADD_FRAGMENT,
        REMOVE_FRAGMENT,
        CLEAR_ALL_AND_ADD_NEW_FRAGMENT
    }

    private var callBacks: CallBacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initializeCallBacks(context)
    }

    override fun onDetach() {
        deleteCallBacks()
        super.onDetach()
    }

    abstract fun getUniqueTag(): String?

    fun handleBackButtonPressed(): Boolean {
        return false
    }

    protected open fun changeFragment(fragment: GPFragment, tType: TransactionType) {
        callBacks?.changeFragment(fragment, tType)
    }

    protected open fun showToastMessage(message: String?) {
        callBacks?.showToastMessage(message)
    }

    private fun initializeCallBacks(context: Context) {
        callBacks = if (context is CallBacks) {
            context
        } else {
            throw RuntimeException("Must implement GPFragment.CallBacks")
        }
    }

    private fun deleteCallBacks() {
        callBacks = null
    }

}