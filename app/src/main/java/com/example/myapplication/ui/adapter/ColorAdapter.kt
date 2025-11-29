package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.databinding.ItemColorBinding
import com.example.myapplication.extention.setRadius
import com.example.myapplication.ui.custom.toPx

class ColorAdapter(listColors: ArrayList<Int>) : BaseAdapter<ItemColorBinding, Int>(listColors) {

    var onClickEvent: ((Int) -> Unit)? = null

    override fun binding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean,
        viewType: Int
    ): ItemColorBinding {
        return ItemColorBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemColorBinding, position: Int) {
        data.getOrNull(position)?.let { color ->
            binding.view.setBackgroundColor(color)
            binding.view.setRadius(24f.toPx.toInt())
            binding.root.setOnClickListener {
                onClickEvent?.invoke(color)
            }
        }
    }
}