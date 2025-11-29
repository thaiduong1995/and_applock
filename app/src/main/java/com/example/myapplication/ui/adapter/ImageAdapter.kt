package com.example.myapplication.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.databinding.ItemSingleImageBinding
import com.example.myapplication.utils.Constants


class ImageAdapter(
    listImage: ArrayList<String> = arrayListOf()
) : BaseAdapter<ItemSingleImageBinding, String>(listImage) {

    private var imageSize = 0
    private var imageType = ImageType.ASSET
    private var showButtonDelete: Boolean = false
    var onClickEvent: ((String, Int) -> Unit)? = null
    var onClickDelete: ((String, Int) -> Unit)? = null

    override fun binding(
        inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean, viewType: Int
    ): ItemSingleImageBinding {
        return ItemSingleImageBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemSingleImageBinding, position: Int) {
        data.getOrNull(position)?.let { imagePath ->
            if (imageType == ImageType.ASSET) {
                Glide.with(binding.root.context)
                    .load(Uri.parse(Constants.ASSET_PATH.plus(imagePath))).into(binding.imgIcon)
            } else {
                Glide.with(binding.root.context).load(imagePath).into(binding.imgIcon)
            }
            binding.root.setOnClickListener {
                onClickEvent?.invoke(imagePath, position)
            }

            if (imageType == ImageType.ASSET) {
                binding.imgDelete.setImageResource(R.drawable.ic_vip)
                binding.imgDelete.isVisible =
                    position != 0 && !CemAdManager.getInstance(binding.imgDelete.context).isVip()
            } else {
                binding.imgDelete.isVisible = showButtonDelete
                binding.imgDelete.setOnClickListener {
                    onClickDelete?.invoke(imagePath, position)
                }
            }
        }
    }

    fun setImageSize(imageSize: Int) {
        this.imageSize = imageSize
    }

    fun setImageTypeLoading(type: ImageType) {
        this.imageType = type
    }

    fun showButtonDelete(isShow: Boolean) {
        this.showButtonDelete = isShow
        notifyDataSetChanged()
    }
}

enum class ImageType {
    ASSET, FILE
}