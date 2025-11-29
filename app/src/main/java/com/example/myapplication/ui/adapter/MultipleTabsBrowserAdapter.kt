package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.data.model.TabBrowser
import com.example.myapplication.databinding.ItemTabBrowserBinding
import com.example.myapplication.utils.stringToBitMap
import com.example.myapplication.utils.setSingleClick

class MultipleTabsBrowserAdapter(
    listTabs: ArrayList<TabBrowser> = arrayListOf(),
    var onClickTab: (index: Int) -> Unit = {},
    var onClickRemove: (id: Long) -> Unit = {},
) : BaseAdapter<ItemTabBrowserBinding, TabBrowser>(listTabs) {

    override fun binding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean,
        viewType: Int
    ): ItemTabBrowserBinding {
        return ItemTabBrowserBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemTabBrowserBinding, position: Int) {
        val item = data[position]
        binding.ivTabBrowser.setImageBitmap(item.image.stringToBitMap())
        binding.ivRemove.setSingleClick { onClickRemove(item.id) }
        binding.ivTabBrowser.setSingleClick { onClickTab(position) }
    }
}