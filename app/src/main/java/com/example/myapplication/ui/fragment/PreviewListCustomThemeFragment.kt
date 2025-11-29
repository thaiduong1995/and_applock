package com.example.myapplication.ui.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.data.model.CustomTheme
import com.example.myapplication.databinding.FragmentDetailsThemeBinding
import com.example.myapplication.extention.addOnPageChangeCallback
import com.example.myapplication.extention.parcelableArrayList
import com.example.myapplication.extention.toastMessageShortTime
import com.example.myapplication.ui.adapter.CommonPagerAdapter
import com.example.myapplication.utils.Constants
import com.example.myapplication.view_model.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreviewListCustomThemeFragment : BaseCacheFragment<FragmentDetailsThemeBinding>() {

    private val viewModel by activityViewModels<ThemeViewModel>()
    private val listCustomTheme: ArrayList<CustomTheme> = arrayListOf()
    private var currentTheme: CustomTheme? = null
    private var position = 0

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentDetailsThemeBinding {
        return FragmentDetailsThemeBinding.inflate(layoutInflater, container, false)
    }

    override fun getDataBundle() {
        listCustomTheme.apply {
            clear()
            arguments?.parcelableArrayList<CustomTheme>(Constants.KEY_THEME_PREVIEW)?.let {
                addAll(it)
            }
        }
        position = arguments?.getInt(Constants.KEY_POSITION) ?: 0
    }

    override fun initUI() {
        listCustomTheme.let { listCustomTheme ->
            val listFragment = listCustomTheme.map { it.previewImagePath }
                .map { PreviewImageFragment.newInstance(it, false) }
            val adapter = CommonPagerAdapter(childFragmentManager, lifecycle)
            adapter.setListFragment(listFragment)
            binding.viewPager.adapter = adapter
            binding.viewPager.offscreenPageLimit = listFragment.size
            binding.viewPager.addOnPageChangeCallback { position ->
                currentTheme = listCustomTheme.getOrNull(position)
            }

            binding.viewPager.post {
                binding.viewPager.setCurrentItem(position, false)
            }
        }
    }

    override fun initListener() {
        binding.imgBack.setOnClickListener {
            popBackStack()
        }

        binding.tvSave.setOnClickListener {
            currentTheme?.let {
                context?.toastMessageShortTime(getString(R.string.change_theme_success))
                viewModel.saveCustomTheme(it)
                viewModel.getTheme()
                popBackStack()
            }
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