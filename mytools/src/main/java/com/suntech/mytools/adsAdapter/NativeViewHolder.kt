package com.suntech.mytools.adsAdapter

import androidx.recyclerview.widget.RecyclerView
import com.suntech.mytools.databinding.ItemNativeBinding
import com.suntech.mytools.mytools.nativeAd.NativeManager
import com.suntech.mytools.tools.gone
import com.suntech.mytools.tools.visible

open class NativeViewHolder(val binding: ItemNativeBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindNativeViewHolder() {
        val native = NativeManager.getNative()
        if (native == null) {
            binding.linearNative.removeAllViews()
            binding.linearNative.gone()
        } else {
            val nativeView = NativeManager.showNative(binding.root.context, native)
            binding.linearNative.visible()
            binding.linearNative.apply {
                removeAllViews()
                addView(nativeView)
            }
        }
    }
}