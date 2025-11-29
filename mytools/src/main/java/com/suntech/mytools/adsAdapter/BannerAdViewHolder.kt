package com.suntech.mytools.adsAdapter

import androidx.recyclerview.widget.RecyclerView
import com.suntech.mytools.databinding.ItemBannerBinding
import com.suntech.mytools.mytools.banner.BannerManager
import com.suntech.mytools.tools.gone

open class BannerAdViewHolder(val binding: ItemBannerBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind() {
        BannerManager.loadBannerAdView(binding.root.context, onLoaded = {
            binding.fragmentLayout.removeAllViews()
            binding.fragmentLayout.addView(it)
        }, onFailedToLoadAll = {
            binding.fragmentLayout.removeAllViews()
            binding.fragmentLayout.gone()
        })
    }
}