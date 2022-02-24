package com.ashtray.appscheduler.features.permissionpage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ashtray.appscheduler.R
import com.ashtray.appscheduler.common.GPFragment
import com.ashtray.appscheduler.common.GPLog
import com.ashtray.appscheduler.common.GPLog.d
import com.ashtray.appscheduler.common.GPLog.e
import com.ashtray.appscheduler.databinding.FragmentPermissionBinding
import com.ashtray.appscheduler.features.home.HomeFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class PermissionFragment: GPFragment() {

    companion object {
        private const val TAG = "PermissionFragment"

        fun newInstance(): PermissionFragment {
            val args = Bundle()
            val fragment = PermissionFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getUniqueTag(): String = TAG

    private lateinit var viewModel: PermissionViewModel
    private lateinit var binding: FragmentPermissionBinding

    private val launchSomeActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        d(TAG, "result received ${result.resultCode}, ${Settings.canDrawOverlays(context)}")
        if (Settings.canDrawOverlays(context)) {
            lifecycleScope.launch(Dispatchers.Main) {
                changeFragment(
                    HomeFragment.newInstance(),
                    TransactionType.CLEAR_ALL_AND_ADD_NEW_FRAGMENT
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        GPLog.d(TAG, "onCreateView: called")
        viewModel = ViewModelProvider(this).get(PermissionViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_permission, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvPermissionButton.setOnClickListener {
            try {
                launchSomeActivity.launch( Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context?.packageName)
                ))
            } catch (e: Exception) {
                e(TAG, "permissionBtnPressed: problem occurs")
                showToastMessage("Can't open permission page")
                e.printStackTrace()
            }
        }
    }

}