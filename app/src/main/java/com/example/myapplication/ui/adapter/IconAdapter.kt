package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.data.model.Icon
import com.example.myapplication.databinding.ItemIconBinding

class IconAdapter(
    listIcon: ArrayList<Icon> = arrayListOf(),
    var onSelectIcon: (Icon) -> Unit = {}
) : BaseAdapter<ItemIconBinding, Icon>(listIcon) {

    override fun binding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean,
        viewType: Int
    ): ItemIconBinding {
        return ItemIconBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemIconBinding, position: Int) {
        data.getOrNull(position)?.let { icon ->
            Glide.with(binding.root.context).load(icon.resId).into(binding.imgIcon)
            binding.imgSelected.isVisible = icon.selected
            binding.root.setOnClickListener {
                data.onEach { it.selected = false }
                icon.selected = true
                onSelectIcon.invoke(icon)
                notifyDataSetChanged()
            }
        }
    }

    fun clearSelection() {
        data.onEach { it.selected = false }
        notifyDataSetChanged()
    }
}