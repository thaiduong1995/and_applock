package com.example.myapplication.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.databinding.ActivityLockFakeCoverBinding
import com.example.myapplication.service.ServiceStarter
import com.example.myapplication.utils.Constants.KEY_LONG_CLICK_SUCCESS
import com.example.myapplication.utils.Constants.KEY_PACKAGE_NAME
import com.example.myapplication.utils.IntentHelper
import com.example.myapplication.utils.PreferenceHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LockFakeCoverActivity : BaseActivity(), View.OnClickListener {

    @Inject
    lateinit var preference: PreferenceHelper

    private lateinit var binding: ActivityLockFakeCoverBinding
    private var isActivityShowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.transparent)
        window.statusBarColor = ContextCompat.getColor(this, R.color.transparent)
        binding = ActivityLockFakeCoverBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.root.setOnClickListener(this)
        binding.layoutFakeCover.tvConfirm.setOnClickListener(this)
        binding.layoutFakeCover.tvConfirm.setOnLongClickListener {
            intent.getStringExtra(KEY_PACKAGE_NAME)?.let {
                val bundle = Bundle().apply {
                    putString(KEY_LONG_CLICK_SUCCESS, it)
                }
                ServiceStarter.startServiceWithData(this, bundle)
            }
            finish()
            true
        }
        binding.layoutFakeCover.tvTitle.text = getString(preference.getRecommendSignals())
    }

    override fun onStart() {
        super.onStart()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    override fun onResume() {
        super.onResume()
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                isActivityShowing = true
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(IntentHelper.launcherIntent())
        finish()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tvConfirm -> {
                startActivity(IntentHelper.launcherIntent())
                finish()
            }
        }
    }


    private fun startLockActivity(lockedAppPackageName: String) {
        val intent = LockActivity.newIntent(applicationContext, lockedAppPackageName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    companion object {
        fun newIntent(context: Context, packageName: String): Intent {
            val intent = Intent(context, LockFakeCoverActivity::class.java)
            intent.putExtra(KEY_PACKAGE_NAME, packageName)
            return intent
        }
    }
}