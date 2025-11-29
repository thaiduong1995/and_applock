package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.data.model.DayItem
import com.example.myapplication.databinding.ItemDayOfWeekBinding

class DayOfWeekAdapter(
    listDay: ArrayList<DayItem>, var onClick: (DayItem, Int) -> Unit
) : BaseAdapter<ItemDayOfWeekBinding, DayItem>(listDay) {

    override fun binding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean,
        viewType: Int
    ): ItemDayOfWeekBinding {
        return ItemDayOfWeekBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemDayOfWeekBinding, position: Int) {
        val item = data[position]
        binding.tvDay.text = item.name
        binding.ivTimeSelected.isSelected = item.isSelected
        binding.ivTimeSelected.setImageResource(if (item.isSelected) R.drawable.day_selector else R.drawable.day_unselected)
        binding.ivTimeSelected.setOnClickListener {
            if (item.isSelected) {
                item.isSelected = false
                binding.ivTimeSelected.setImageResource(R.drawable.day_unselected)
            } else {
                item.isSelected = true
                binding.ivTimeSelected.setImageResource(R.drawable.day_selector)
            }
            onClick.invoke(item, position)
        }
    }
}