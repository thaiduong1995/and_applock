package com.example.myapplication.ui.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.data.model.LockType
import com.example.myapplication.data.model.ThemeData
import com.example.myapplication.data.model.ThemePreview
import com.example.myapplication.databinding.FragmentMoreThemeBinding
import com.example.myapplication.extention.parcelableArrayList
import com.example.myapplication.ui.adapter.ImageAdapter
import com.example.myapplication.ui.custom.LinearItemDecoration
import com.example.myapplication.ui.custom.toPx
import com.example.myapplication.utils.Constants

class MoreThemeFragment : BaseCacheFragment<FragmentMoreThemeBinding>() {

    private var themePreview: ArrayList<ThemePreview>? = arrayListOf()
    private var listImagePath = ArrayList<String>()
    private var lockType: LockType? = LockType.PASS_CODE

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMoreThemeBinding {
        return FragmentMoreThemeBinding.inflate(inflater, container, false)
    }

    override fun getDataBundle() {
        themePreview = arguments?.parcelableArrayList(Constants.KEY_THEME_PREVIEW)
        lockType =
            arguments?.getSerializable(Constants.KEY_THEME_PREVIEW_TYPE) as? LockType
    }

    override fun initData() {
        themePreview?.let { data ->
            when (lockType) {
                LockType.PASS_CODE -> {
                    binding.tvTitle.text = getString(R.string.pass_code)
                    listImagePath.addAll(data.flatMap { it.image }.filter {
                        it.contains(ThemeData.PREVIEW_PIN)
                    })
                }

                LockType.PATTERN -> {
                    binding.tvTitle.text = getString(R.string.pattern)
                    listImagePath.addAll(data.flatMap { it.image }.filter {
                        it.contains(ThemeData.PREVIEW_PATTERN)
                    })
                }

                LockType.KNOCK -> {
                    binding.tvTitle.text = getString(R.string.knock_code)
                    listImagePath.addAll(data.flatMap { it.image }.filter {
                        it.contains(ThemeData.PREVIEW_KNOCK)
                    })
                }

                else -> {}
            }
        }
    }

    override fun initUI() {
        val adapter = ImageAdapter(listImagePath)
        adapter.setImageSize(resources.displayMetrics.widthPixels / 2)
        binding.recycler.layoutManager = GridLayoutManager(context, 2)
        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(
            LinearItemDecoration(
                4f.toPx.toInt(),
                4f.toPx.toInt()
            )
        )
        adapter.onClickEvent = { imagePath, position ->
            val bundle = bundleOf(
                Constants.KEY_THEME_PREVIEW to themePreview,
                Constants.KEY_THEME_PREVIEW_TYPE to lockType,
                Constants.KEY_POSITION to position
            )
            findNavController().navigate(R.id.detailsThemeScreen, bundle)
        }
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