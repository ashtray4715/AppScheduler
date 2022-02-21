package com.ashtray.appscheduler.features.appselector

import android.annotation.SuppressLint
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
import android.content.pm.ApplicationInfo

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
        binding.rvAppList.apply {
            layoutManager =  LinearLayoutManager(context)
            adapter = AppSelectorAdapter(context).apply {
                setAppList(getAllTheUserInstalledApp())
            }
        }

        return binding.root
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getAllTheUserInstalledApp(): MutableList<MyAppInfo> {
        val returnAppList = mutableListOf<MyAppInfo>()
        context?.packageManager?.let { packageManager ->
            val allPkgList = packageManager.getInstalledPackages(0)
            for (pkgInfo in allPkgList) {
                val currentAppInfo = pkgInfo.applicationInfo
                if(currentAppInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                    returnAppList.add(
                        MyAppInfo(
                            currentAppInfo.loadLabel(packageManager).toString(),
                            pkgInfo.packageName,
                            context?.packageManager?.getApplicationIcon(currentAppInfo.packageName)
                        )
                    )
                }
            }
        }
        return returnAppList
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            actionBar.setBackListener { handleBackButtonPressed() }
            cancelButton.setOnClickListener { handleBackButtonPressed() }
            selectButton.setOnClickListener { appSelectedBtnPressed() }
        }
    }

    private fun appSelectedBtnPressed() {

    }

    override fun handleBackButtonPressed(): Boolean {
        val appId = "com.ashtray.appscheduler"
        setFragmentResult(GPConst.PK_APP_ID, bundleOf(GPConst.PK_APP_ID to  appId))
        changeFragment(this, TransactionType.REMOVE_FRAGMENT)
        return true
    }

}