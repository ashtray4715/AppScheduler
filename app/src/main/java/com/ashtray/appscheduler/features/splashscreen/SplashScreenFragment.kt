package com.ashtray.appscheduler.features.splashscreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ashtray.appscheduler.R
import com.ashtray.appscheduler.common.GPFragment
import com.ashtray.appscheduler.common.GPLog
import com.ashtray.appscheduler.databinding.FragmentSplashScreenBinding
import com.ashtray.appscheduler.features.home.HomeFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.provider.Settings
import com.ashtray.appscheduler.features.permissionpage.PermissionFragment


@SuppressLint("CustomSplashScreen")
class SplashScreenFragment: GPFragment() {

    companion object {
        private const val TAG = "SplashScreenFragment"

        fun newInstance(): SplashScreenFragment {
            val args = Bundle()
            val fragment = SplashScreenFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getUniqueTag(): String = TAG

    private lateinit var viewModel: SplashScreenViewModel
    private lateinit var binding: FragmentSplashScreenBinding

    private var splashTaskCompletedCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        GPLog.d(TAG, "onCreateView: called")
        viewModel = ViewModelProvider(this).get(SplashScreenViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash_screen, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getCompletedTaskListLiveData().observe(viewLifecycleOwner, {
            lifecycleScope.launch(Dispatchers.IO) {
                loadHomePage()
            }
        })
        viewModel.getRemainingTaskListLiveData().observe(viewLifecycleOwner, {
            lifecycleScope.launch(Dispatchers.IO) {
                loadHomePage()
            }
        })
        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch(Dispatchers.IO) {
                loadHomePage()
            }
        }, 1000)
    }

    @Synchronized fun loadHomePage() {
        splashTaskCompletedCount++
        if(splashTaskCompletedCount == 3) {
            lifecycleScope.launch(Dispatchers.Main) {
                changeFragment(
                    when(Settings.canDrawOverlays(context)) {
                        true -> HomeFragment.newInstance()
                        else -> PermissionFragment.newInstance()
                    }, TransactionType.CLEAR_ALL_AND_ADD_NEW_FRAGMENT
                )
            }
        }
    }
}