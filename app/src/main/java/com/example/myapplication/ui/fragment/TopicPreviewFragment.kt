package com.example.myapplication.ui.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.data.model.ThemePreview
import com.example.myapplication.data.model.ThemeTopic
import com.example.myapplication.data.model.liveData.StateData
import com.example.myapplication.databinding.FragmentTopicPreviewBinding
import com.example.myapplication.extention.FirebaseEvent
import com.example.myapplication.ui.adapter.TopicPreviewAdapter
import com.example.myapplication.utils.Constants
import com.example.myapplication.view_model.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TopicPreviewFragment : BaseCacheFragment<FragmentTopicPreviewBinding>() {

    private val viewModel by viewModels<ThemeViewModel>()
    private var themeTopic: ThemeTopic? = null

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTopicPreviewBinding {
        return FragmentTopicPreviewBinding.inflate(inflater, container, false)
    }

    override fun getDataBundle() {
        themeTopic = arguments?.getSerializable(Constants.KEY_THEME_TOPIC) as ThemeTopic?
    }

    override fun initUI() {
        themeTopic?.let {
            binding.tvTitle.text = getString(it.resName)
        }
    }

    override fun initData() {
        themeTopic?.let {
            viewModel.getDetailsTopic(it)
        }
    }

    override fun initObservers() {
        viewModel.themePreviewLiveData.observe(viewLifecycleOwner) {
            when (it.getStatus()) {
                StateData.DataStatus.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                StateData.DataStatus.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    initRecyclerView(it.getData())
                }

                StateData.DataStatus.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                }

                else -> {}
            }
        }
    }

    private fun initRecyclerView(data: ArrayList<ThemePreview>?) {
        data?.let {
            val adapter = TopicPreviewAdapter(it)
            adapter.onItemClick = { themePreview, previewType, position ->
                FirebaseEvent.viewDetailTheme()
                val bundle = bundleOf(
                    Constants.KEY_THEME_PREVIEW to data,
                    Constants.KEY_THEME_PREVIEW_TYPE to previewType,
                    Constants.KEY_POSITION to position
                )
                findNavController().navigate(R.id.detailsThemeScreen, bundle)
            }

            adapter.onSeeMoreClick = { previewType ->
                val bundle = bundleOf(
                    Constants.KEY_THEME_PREVIEW to data,
                    Constants.KEY_THEME_PREVIEW_TYPE to previewType
                )
                findNavController().navigate(R.id.moreThemeScreen, bundle)
            }
            binding.recycler.layoutManager = LinearLayoutManager(context)
            binding.recycler.adapter = adapter
            adapter.setImageSize(getImageSize())
        }
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
                    override fun onBannerLoaded(banner: BannerAdView, view: View) { }

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

    override fun initListener() {
        binding.tvCustomTheme.setOnClickListener {
            FirebaseEvent.clickCustom(FirebaseEvent.CLICK_CUSTOM_THEME)
            findNavController().navigate(R.id.customThemeScreen)
        }

        binding.imgBack.setOnClickListener {
            popBackStack()
        }
    }

    private fun getImageSize(): Int {
        context?.let { ct ->
            return ct.resources.displayMetrics.widthPixels * 4 / 10
        }
        return 0
    }
}