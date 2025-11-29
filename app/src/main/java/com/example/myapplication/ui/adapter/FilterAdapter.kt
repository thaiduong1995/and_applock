package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.data.model.Filter
import com.example.myapplication.databinding.ItemFilterBinding

class FilterAdapter(
    private val filters: ArrayList<Filter> = arrayListOf(),
    private val onClickEvent: (Filter) -> Unit = {}
) : BaseAdapter<ItemFilterBinding, Filter>(filters) {

    override fun binding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean,
        viewType: Int
    ): ItemFilterBinding {
        return ItemFilterBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemFilterBinding, position: Int) {
        filters.getOrNull(position)?.let { filter ->
            when (filter) {
                Filter.Locked -> {
                    binding.tvFilterName.text = binding.root.context.getString(R.string.locked)
                }

                Filter.UnLocked -> {
                    binding.tvFilterName.text = binding.root.context.getString(R.string.unlocked)
                }

                Filter.AZ -> {
                    binding.tvFilterName.text = binding.root.context.getString(R.string.az)
                }

                Filter.ZA -> {
                    binding.tvFilterName.text = binding.root.context.getString(R.string.za)
                }

                Filter.Newest -> {
                    binding.tvFilterName.text = binding.root.context.getString(R.string.newest)
                }

                Filter.Oldest -> {
                    binding.tvFilterName.text = binding.root.context.getString(R.string.oldEst)
                }
            }
            if (filter.isSelected) {
                binding.root.setBackgroundResource(R.drawable.bg_layout_search)
            } else {
                binding.root.setBackgroundResource(R.drawable.bg_transparent)
            }
            binding.root.setOnClickListener {
                onClickEvent.invoke(filter)
            }
        }
    }
}