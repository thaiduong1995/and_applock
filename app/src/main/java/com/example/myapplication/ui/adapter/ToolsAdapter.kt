package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.data.model.Tools
import com.example.myapplication.databinding.ItemToolsBinding

class ToolsAdapter(
    listTool: ArrayList<Tools> = arrayListOf(), var onClickAction: (Tools) -> Unit = {}
) : BaseAdapter<ItemToolsBinding, Tools>(listTool) {

    override fun binding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean,
        viewType: Int
    ): ItemToolsBinding {
        return ItemToolsBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemToolsBinding, position: Int) {
        data.getOrNull(position)?.let { tools ->
            binding.imgIcon.setImageResource(tools.resIcon)
            binding.tvName.text = binding.root.context.getString(tools.resToolName)
            binding.root.setOnClickListener { onClickAction(tools) }
        }
    }
}