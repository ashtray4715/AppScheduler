package com.ashtray.appscheduler.features.dateselector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import com.ashtray.appscheduler.R
import com.ashtray.appscheduler.common.GPConst
import com.ashtray.appscheduler.common.GPFragment
import com.ashtray.appscheduler.common.GPLog.d
import com.ashtray.appscheduler.databinding.FragmentDateSelectorBinding

class DateSelectorFragment: GPFragment() {

    companion object {
        private const val TAG = "DateSelectorFragment"

        fun newInstance(): DateSelectorFragment {
            val args = Bundle()
            val fragment = DateSelectorFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getUniqueTag(): String {
        return TAG
    }

    private lateinit var viewModel: DateSelectorViewModel
    private lateinit var binding: FragmentDateSelectorBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        d(TAG, "onCreateView: called")
        viewModel = ViewModelProvider(this).get(DateSelectorViewModel::class.java)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_date_selector,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            actionBar.setBackListener { handleBackButtonPressed() }
            cancelButton.setOnClickListener { handleBackButtonPressed() }
            selectButton.setOnClickListener { dateSelectedBtnPressed() }
        }
    }

    private fun dateSelectedBtnPressed() {
        val dPicker = binding.datePicker
        val dateValue = "${dPicker.dayOfMonth}_${dPicker.month + 1}_${dPicker.year}"
        setFragmentResult(GPConst.PK_DATE, bundleOf(GPConst.PK_DATE to  dateValue))
        changeFragment(this, TransactionType.REMOVE_FRAGMENT)
    }

    override fun handleBackButtonPressed(): Boolean {
        setFragmentResult(
            GPConst.PK_DATE,
            bundleOf(GPConst.PK_DATE to  GPConst.MSG_NO_DATE_SELECTED)
        )
        changeFragment(this, TransactionType.REMOVE_FRAGMENT)
        return true
    }


}