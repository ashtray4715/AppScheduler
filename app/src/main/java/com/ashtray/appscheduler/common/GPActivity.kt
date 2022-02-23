package com.ashtray.appscheduler.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ashtray.appscheduler.R

import com.ashtray.appscheduler.common.GPLog.d

import androidx.fragment.app.FragmentTransaction
import com.ashtray.appscheduler.common.GPFragment.TransactionType
import com.ashtray.appscheduler.common.GPLog.e
import java.lang.Exception
import android.widget.Toast
import com.ashtray.appscheduler.features.splashscreen.SplashScreenFragment

class GPActivity : AppCompatActivity(), GPFragment.CallBacks {

    companion object {
        private const val TAG = "GPActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        changeFragment(
            SplashScreenFragment.newInstance(),
            TransactionType.CLEAR_ALL_AND_ADD_NEW_FRAGMENT
        )
    }

    override fun changeFragment(fragment: GPFragment, transactionType: TransactionType) {
        try {
            when (transactionType) {
                TransactionType.ADD_FRAGMENT -> {
                    d(TAG, "changeFragment: add, " + fragment.getUniqueTag())
                    val ft1: FragmentTransaction = supportFragmentManager.beginTransaction()
                    ft1.setCustomAnimations(R.anim.scale_in, R.anim.scale_out)
                    ft1.add(R.id.fragment_container, fragment, fragment.getUniqueTag())
                    ft1.commit()
                }
                TransactionType.REMOVE_FRAGMENT -> {
                    d(TAG, "changeFragment: remove, " + fragment.getUniqueTag())
                    val ft2: FragmentTransaction = supportFragmentManager.beginTransaction()
                    ft2.setCustomAnimations(R.anim.scale_in, R.anim.scale_out)
                    ft2.remove(fragment)
                    ft2.commit()
                }
                TransactionType.CLEAR_ALL_AND_ADD_NEW_FRAGMENT -> {
                    d(TAG, "changeFragment: clear_add, " + fragment.getUniqueTag())
                    for (oldF in supportFragmentManager.fragments) {
                        supportFragmentManager.beginTransaction().remove(oldF).commitNow()
                    }
                    val ft3: FragmentTransaction = supportFragmentManager.beginTransaction()
                    ft3.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    ft3.add(R.id.fragment_container, fragment, fragment.getUniqueTag())
                    ft3.commit()
                }
            }
        } catch (e: Exception) {
            e(TAG, "changeFragment: problem occurs [${e.message}]")
            e.printStackTrace()
        }
    }

    override fun showToastMessage(message: String?) {
        try {
            runOnUiThread {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e(TAG, "showToastMessage: problem occurs [${e.message}]")
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as GPFragment?
        if (fragment != null && fragment.handleBackButtonPressed()) {
            d(TAG, "handled back press inside fragment, " + fragment.getUniqueTag())
            return
        }
        d(TAG, "fragment can't handle back press [CLOSING_APP]")
        super.onBackPressed()
    }
}