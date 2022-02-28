package com.ashtray.appscheduler.features.appselector

import android.annotation.SuppressLint
import android.content.Intent
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
import com.ashtray.appscheduler.databinding.FragmentAppSelectorBinding

import androidx.recyclerview.widget.LinearLayoutManager

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

    private lateinit var viewModel: AppSelectorViewModel
    private lateinit var binding: FragmentAppSelectorBinding

    private var listAdapter: AppSelectorAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        d(TAG, "onCreateView: called")
        viewModel = ViewModelProvider(this).get(AppSelectorViewModel::class.java)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_app_selector,
            container,
            false
        )
        listAdapter = AppSelectorAdapter(context).apply {
            setAppList(getAllTheUserInstalledApp())
        }
        binding.rvAppList.apply {
            layoutManager =  LinearLayoutManager(context)
            adapter = listAdapter
        }

        return binding.root
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getAllTheUserInstalledApp(): MutableList<MyAppInfo> {
        val returnAppList = mutableListOf<MyAppInfo>()
        context?.packageManager?.let { packageManager ->
            val allResolveInfo = packageManager.queryIntentActivities(
                Intent(Intent.ACTION_MAIN, null).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }, 0
            )
            for (resolveInfo in allResolveInfo) {
                val currentAppInfo = resolveInfo.activityInfo.applicationInfo
                returnAppList.add(
                    MyAppInfo(
                        currentAppInfo.loadLabel(packageManager).toString(),
                        currentAppInfo.packageName,
                        context?.packageManager?.getApplicationIcon(currentAppInfo.packageName)
                    )
                )
            }
        }
        return returnAppList
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            actionBar.setBackListener { handleBackButtonPressed() }
            actionBar.setMenuListener1 { appSelectedBtnPressed() }
            cancelButton.setOnClickListener { handleBackButtonPressed() }
            selectButton.setOnClickListener { appSelectedBtnPressed() }
        }
    }

    private fun appSelectedBtnPressed() {
        listAdapter?.getSelectedApp()?.let { myAppInfo ->
            setFragmentResult(
                GPConst.PK_APP_ID,
                bundleOf(GPConst.PK_APP_ID to  myAppInfo.appPkgName)
            )
        }
        changeFragment(this, TransactionType.REMOVE_FRAGMENT)
    }

    override fun handleBackButtonPressed(): Boolean {
        changeFragment(this, TransactionType.REMOVE_FRAGMENT)
        return true
    }

}