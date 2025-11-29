package com.example.myapplication.ui.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.databinding.FragmentHowToUseBinding

class HowToUseFragment : BaseCacheFragment<FragmentHowToUseBinding>() {
    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHowToUseBinding {
        return FragmentHowToUseBinding.inflate(inflater, container, false)
    }

    override fun initListener() {
        binding.imgBack.setOnClickListener {
            popBackStack()
        }
    }

    override fun loadAds() {
        activity?.let { act ->
            CemAdManager.getInstance(act).loadBannerAndShowByActivity(
                activity = act,
                viewGroup = binding.layoutBanner.bannerLayout,
                configKey = ConstAd.BANNER_KEY_DETAIL,
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