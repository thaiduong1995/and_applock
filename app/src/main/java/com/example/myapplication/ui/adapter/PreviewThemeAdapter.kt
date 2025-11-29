package com.example.myapplication.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.data.model.LockType
import com.example.myapplication.data.model.ThemeData
import com.example.myapplication.data.model.ThemePreview
import com.example.myapplication.databinding.ItemSingleImageBinding
import com.example.myapplication.utils.Constants

class PreviewThemeAdapter(
    listTheme: ArrayList<ThemePreview> = arrayListOf()
) : BaseAdapter<ItemSingleImageBinding, ThemePreview>(listTheme) {

    private var imageSize = 0
    private var lockType: LockType = LockType.PASS_CODE
    var onClickEvent: ((ThemePreview, LockType, Int) -> Unit)? = null

    override fun binding(
        inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean, viewType: Int
    ): ItemSingleImageBinding {
        return ItemSingleImageBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemSingleImageBinding, position: Int) {
        data.getOrNull(position)?.let { themePreview ->
            binding.imgIcon.layoutParams.width = imageSize
            var imagePath: String? = null
            imagePath = when (lockType) {
                LockType.PASS_CODE -> {
                    themePreview.image.find { it.contains(ThemeData.PREVIEW_PIN) }
                }

                LockType.PATTERN -> {
                    themePreview.image.find { it.contains(ThemeData.PREVIEW_PATTERN) }
                }

                LockType.KNOCK -> {
                    themePreview.image.find { it.contains(ThemeData.PREVIEW_KNOCK) }
                }
            }
            imagePath?.let {
                Glide.with(binding.root.context).load(Uri.parse(Constants.ASSET_PATH.plus(it)))
                    .into(binding.imgIcon)
            }
            binding.imgDelete.setImageResource(R.drawable.ic_vip)
            binding.imgDelete.isVisible =
                position != 0 && !CemAdManager.getInstance(binding.imgDelete.context).isVip()
            binding.root.setOnClickListener {
                onClickEvent?.invoke(themePreview, lockType, position)
            }
        }
    }

    fun setImageSize(imageSize: Int) {
        this.imageSize = imageSize
    }

    fun setItemType(lockType: LockType) {
        this.lockType = lockType
        notifyDataSetChanged()
    }
}