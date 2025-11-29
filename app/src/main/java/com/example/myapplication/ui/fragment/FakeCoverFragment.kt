package com.example.myapplication.ui.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.data.model.liveData.StateData
import com.example.myapplication.databinding.FragmentFakeCoverBinding
import com.example.myapplication.extention.FirebaseEvent
import com.example.myapplication.ui.adapter.CommonSelectorAdapter
import com.example.myapplication.view_model.FakeCoverViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FakeCoverFragment : BaseCacheFragment<FragmentFakeCoverBinding>() {

    private val viewModel by viewModels<FakeCoverViewModel>()
    private val adapter = CommonSelectorAdapter()

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentFakeCoverBinding {
        return FragmentFakeCoverBinding.inflate(inflater, container, false)
    }

    override fun initUI() {
        binding.swFakeCover.isChecked = viewModel.isFakeCoverEnabled()
        initRecyclerAdapter()
    }

    private fun initRecyclerAdapter() {
        binding.recycler.layoutManager = LinearLayoutManager(context)
        binding.recycler.adapter = adapter
        adapter.onClickCallback = {
            FirebaseEvent.chooseCover(it.name)
            it.idString?.let { it1 -> viewModel.saveRecommendSignal(it1) }
            binding.layoutFakeCover.tvTitle.text = it.name
        }
    }

    override fun initListener() {
        binding.imgBack.setOnClickListener { popBackStack() }
        binding.swFakeCover.setOnClickListener {
            val enable = !viewModel.isFakeCoverEnabled()
            FirebaseEvent.switchCover(enable)
            binding.swFakeCover.isChecked = enable
            viewModel.setEnableFakeCover(enable)
        }
    }

    override fun initData() {
        context?.let {
            viewModel.getRecommendSignal(it)
        }
    }

    override fun initObservers() {
        viewModel.listRecommendLiveData.observe(this) {
            when (it.getStatus()) {
                StateData.DataStatus.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                StateData.DataStatus.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    it.getData()?.let {
                        adapter.setData(it)
                        binding.layoutFakeCover.tvTitle.text =
                            it.firstOrNull { it.isSelected }?.name
                    }
                }

                StateData.DataStatus.ERROR -> {
                    binding.progressBar.visibility = View.GONE

                }

                else -> {}
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