package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.data.model.MediaItem
import com.example.myapplication.databinding.ItemMediaBinding

class MediaAdapter(
    listMedia: MutableList<MediaItem> = mutableListOf(),
    var onClick: (String) -> Unit = {}
) : BaseAdapter<ItemMediaBinding, MediaItem>(listMedia as ArrayList<MediaItem>) {

    override fun binding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean,
        viewType: Int
    ): ItemMediaBinding {
        return ItemMediaBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemMediaBinding, position: Int) {
        val itemMedia = data[position]
        binding.ivMedia.setImageResource(itemMedia.logo)
        binding.tvTitle.text = binding.root.context.getString(itemMedia.title)
        binding.root.setOnClickListener {
            onClick(itemMedia.url)
        }
    }
}