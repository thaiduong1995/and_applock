package com.example.myapplication.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.databinding.FragmentFaqBinding
import com.example.myapplication.ui.adapter.FAQAdapter
import com.example.myapplication.view_model.FAQViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FAQFragment : BaseCacheFragment<FragmentFaqBinding>() {

    private val viewModel by viewModels<FAQViewModel>()
    private val adapter = FAQAdapter()

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): FragmentFaqBinding {
        return FragmentFaqBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rcFAQ.adapter = adapter
        adapter.setList(viewModel.questions)

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