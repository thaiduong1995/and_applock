package com.example.myapplication.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.example.myapplication.base.BaseDialog
import com.example.myapplication.databinding.DialogConfirmRewardBinding
import com.example.myapplication.utils.setSingleClick

class DialogConfirmReward : BaseDialog<DialogConfirmRewardBinding>() {

    var callback: (Boolean) -> Unit = {}

    override fun onStart() {
        super.onStart()
        isCancelable = true
    }

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?
    ): DialogConfirmRewardBinding {
        return DialogConfirmRewardBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvMessage.text = arguments?.getString(TITLE)
        binding.tvConfirm.setSingleClick {
//            if (RewardedAdUtils.rewardedAdAdmob == null) {
//                binding.tvLoading.visible()
//                binding.tvConfirm.isEnabled = false
//                binding.tvConfirm.setBackgroundResource(R.drawable.bg_stroke_2_color_gray_radius_16)
//                activity?.let {
//                    RewardedAdUtils.loadRewardedAd(context = it, onLoaded = {
//                        binding.tvLoading.gone()
//                        binding.tvConfirm.isEnabled = true
//                        binding.tvConfirm.setBackgroundResource(R.drawable.bg_radius_100_color_blue)
//                    }, onFailed = {
//                        binding.tvConfirm.isEnabled = true
//                        it.toast(it.getString(R.string.loading_ads_failed))
//                        binding.tvLoading.text = it.getString(R.string.loading_ads_failed)
//                    })
//                }
//            } else {
//                callbackConfirm?.invoke()
            callback(true)
            dismiss()
//            }
        }
        binding.tvGetPremium.setSingleClick {
            callback(false)
            dismiss()
        }

        binding.imvClose.setOnClickListener {
            callback(false)
            dismiss()
        }

        binding.layoutNative.apply {
            //linearNative.isVisible = !DataLocal.isVip()
            activity?.let { act ->
//                val nativeAd = NativeManager.getNative()
//                if (nativeAd == null) {
//                    NativeManager.createNativesAds(act, onLoaded = { it ->
//                        linearNative.visible()
//                        linearNative.removeAllViews()
//                        val nativeView = NativeManager.showNative(act, it)
//                        linearNative.addView(nativeView)
//                    }, onLoadFailed = {
//                        linearNative.gone()
//                    })
//                } else {
//                    linearNative.removeAllViews()
//                    val nativeView = NativeManager.showNative(act, nativeAd)
//                    linearNative.addView(nativeView)
//                }
            }
        }
    }

    companion object {

        private const val TITLE = "TITLE"

        @JvmStatic
        fun newInstance(title: String? = null) = DialogConfirmReward().apply {
            arguments = bundleOf(TITLE to title)
        }
    }
}