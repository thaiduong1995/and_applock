package com.example.myapplication.ui.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.databinding.FragmentCaptureIntrudersBinding
import com.example.myapplication.extention.FirebaseEvent
import com.example.myapplication.ui.dialog.CommonSelectorDialog
import com.example.myapplication.utils.PermissionChecker
import com.example.myapplication.view_model.CaptureIntrudersViewModel
import com.example.myapplication.view_model.MainViewModel

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CaptureIntrudersFragment : BaseCacheFragment<FragmentCaptureIntrudersBinding>() {

    private val viewModel by activityViewModels<CaptureIntrudersViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentCaptureIntrudersBinding {
        return FragmentCaptureIntrudersBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        viewModel.getIntruder()
    }

    override fun initUI() {
        super.initUI()
        val unlockCount = viewModel.getUnlockCount()
        binding.tvUnlockCount.text = getString(R.string.after_time, unlockCount)
        binding.swCaptureIntruders.isChecked = viewModel.isCaptureIntrudersEnabled()
    }


    override fun initListener() {
        binding.imgBack.setOnClickListener(this)
        binding.layoutReviewIntruder.setOnClickListener(this)
        binding.layoutTakePicture.setOnClickListener(this)
        binding.swCaptureIntruders.setOnClickListener(this)
        binding.swCaptureIntruders.setOnCheckedChangeListener { compoundButton, isCheck ->
            context?.let { ct ->
                if (isCheck && !PermissionChecker.isPermissionCameraEnable(ct)) {
                    requestPermission()
                } else {
                    viewModel.setEnableCaptureIntruders(isCheck)
                }
                FirebaseEvent.switchCapture(isCheck)
            }
        }
    }

    override fun onViewClicked(view: View) {
        when (view.id) {
            R.id.imgBack -> popBackStack()
            R.id.layoutReviewIntruder -> {
                FirebaseEvent.clickReview()
                findNavController().navigate(R.id.intruderRecordScreen)
            }

            R.id.layoutTakePicture -> {
                showDialogSelectUnlockCount()
            }

            R.id.swCaptureIntruders -> {
                context?.let { ct ->
                    val enable = !viewModel.isCaptureIntrudersEnabled()
                    if (enable && !PermissionChecker.isPermissionCameraEnable(ct)) {
                        requestPermission()
                    } else {
                        viewModel.setEnableCaptureIntruders(enable)
                        binding.swCaptureIntruders.isChecked = enable
                    }
                    FirebaseEvent.switchCapture(enable)
                }
            }
        }
    }

    private fun requestPermission() {
        activity?.let {
            PermissionChecker.requestCameraPermission(it)
        }
    }

    private fun showDialogSelectUnlockCount() {
        viewModel.listUnlockCountLiveData.value?.let {
            val dialog = CommonSelectorDialog.newInstance(
                getString(R.string.take_a_picture), getString(R.string.on_wrong_unlock)
            )
            if (!dialog.isShown) {
                dialog.show(childFragmentManager, "")
            }
            dialog.onClickEvent = {
                binding.tvUnlockCount.text = context?.getString(R.string.after_time, it.value)
                FirebaseEvent.chooseTime(it.value.toString())
                viewModel.saveUnlockCount(it)
            }
            dialog.setData(it)
        }
    }

    override fun initObservers() {
        viewModel.listImageLiveData?.observe(this) {
            it?.let { data ->
                if (data.isEmpty()) {
                    binding.tvIntruderCount.text = getString(R.string.no_intruder_found)
                } else {
                    binding.tvIntruderCount.text = getString(R.string.intruder_count, data.size)
                }
            }
            binding.progressBar.visibility = View.GONE
        }

        mainViewModel.cameraPermissionLiveData.observe(this) { result ->
            if (result) {
                enableIntruderSelfie()
            } else binding.swCaptureIntruders.isChecked = false
        }
    }

    private fun enableIntruderSelfie() {
        viewModel.setEnableCaptureIntruders(true)
        binding.swCaptureIntruders.isChecked = true
    }

    override fun loadAds() {
        activity?.let { act ->
            CemAdManager.getInstance(act).loadBannerAndShowByActivity(
                activity = act,
                viewGroup = binding.layoutBanner.bannerLayout,
                configKey = ConstAd.BANNER_KEY_HOME,
                position = ConstAd.POS_BOTTOM_BANNER,
                nameScreen = this::class.simpleName,
                callback = object : BannerAdListener {
                    override fun onBannerLoaded(banner: BannerAdView, view: View) {}

                    override fun onBannerFailed(error: String?) {
                        Log.d("onBannerFailed", "$error")
                    }

                    override fun onBannerClicked() {}

                    override fun onBannerOpen() {}

                    override fun onBannerClose() {}

                }
            )
        }
    }
}