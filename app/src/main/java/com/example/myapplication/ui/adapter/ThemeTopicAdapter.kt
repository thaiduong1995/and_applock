package com.example.myapplication.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.data.model.ThemeTopic
import com.example.myapplication.databinding.ItemThemeTopicBinding
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.Utils

class ThemeTopicAdapter(
    arrayList: ArrayList<ThemeTopic> = arrayListOf()
) : BaseAdapter<ItemThemeTopicBinding, ThemeTopic>(arrayList) {

    var onClickEvent: ((ThemeTopic) -> Unit)? = null

    override fun binding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean,
        viewType: Int
    ): ItemThemeTopicBinding {
        return ItemThemeTopicBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemThemeTopicBinding, position: Int) {
        data.getOrNull(position)?.let { themeTopic ->
            binding.tvName.text = binding.root.context.getString(themeTopic.resName)
            val filePath =
                Constants.ASSET_PATH.plus(Utils.getImagePreviewThemeTopic(themeTopic.folderName))
            Glide.with(binding.root.context).load(Uri.parse(filePath)).into(binding.imgIcon)
            binding.root.setOnClickListener {
                onClickEvent?.invoke(themeTopic)
            }
        }
    }
}