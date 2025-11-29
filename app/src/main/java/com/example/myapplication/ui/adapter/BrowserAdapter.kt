package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.data.model.BrowserItem
import com.example.myapplication.databinding.ItemWebBrowserBinding

class BrowserAdapter(listBrowser: ArrayList<BrowserItem>, var onClick: (String) -> Unit) :
    BaseAdapter<ItemWebBrowserBinding, BrowserItem>(listBrowser) {
    override fun binding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean,
        viewType: Int
    ): ItemWebBrowserBinding {
        return ItemWebBrowserBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemWebBrowserBinding, position: Int) {
        val itemBrowser = data[position]
        binding.tvTitle.text = binding.root.context.getString(itemBrowser.title)
        binding.rvMedia.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
        val adapter = MediaAdapter(itemBrowser.mList) {
            onClick(it)
        }
        binding.rvMedia.adapter = adapter
    }
}