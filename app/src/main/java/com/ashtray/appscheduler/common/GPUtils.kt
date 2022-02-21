package com.ashtray.appscheduler.common

import android.content.Context
import android.content.pm.PackageManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.ashtray.appscheduler.common.GPLog.e
import com.ashtray.appscheduler.R

class GPUtils {

    companion object {
        private const val TAG = "GPUtils"
    }

    fun getAppNameFromPkgName(context: Context?, packageName: String?): String {
        return try {
            packageName?.let {
                context?.packageManager?.getApplicationLabel(
                    context.packageManager.getApplicationInfo(
                        packageName,
                        PackageManager.GET_META_DATA
                    )
                ) as String? ?: GPConst.MSG_NO_APP_SELECTED
            } ?: GPConst.MSG_NO_APP_SELECTED
        } catch (e: Exception) {
            e(TAG, "getAppNameFromPkgName: problem occurs [${e.message}]")
            e.printStackTrace()
            GPConst.MSG_NO_APP_SELECTED
        }
    }

    fun showAppIcon(ivAppIcon: ImageView, pkgName: String?, context: Context?) {
        try {
            pkgName?.let {
                context?.packageManager?.getApplicationIcon(it)?.let { icon ->
                    ivAppIcon.setImageDrawable(icon)
                } ?: {
                    e(TAG, "showAppIcon: invalid package name")
                    ivAppIcon.setImageDrawable(
                        ContextCompat.getDrawable(context!!, R.drawable.ic_no_image_24)
                    )
                }
            }
        } catch (e: Exception) {
            e(TAG, "showAppIcon: problem occurs [${e.message}]")
            e.printStackTrace()
            context?.let {
                ivAppIcon.setImageDrawable(
                    ContextCompat.getDrawable(it, R.drawable.ic_no_image_24)
                )
            }
        }
    }
}