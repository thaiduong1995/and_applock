package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.data.model.DayItem
import com.example.myapplication.databinding.ItemDayOfWeekBinding

class DayTimeLockAdapter(
    listDay: ArrayList<DayItem>, var onClick: (DayItem, Int) -> Unit
) : BaseAdapter<ItemDayOfWeekBinding, DayItem>(listDay) {

    override fun binding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean,
        viewType: Int
    ): ItemDayOfWeekBinding {
        return ItemDayOfWeekBinding.inflate(inflater, parent, false)
    }

    override fun onBindData(binding: ItemDayOfWeekBinding, position: Int) {
        binding.apply {
            val item = data[position]
            tvDay.text = item.name.first().toString()
            if (item.isSelected) {
                tvDay.setTextColor(ContextCompat.getColor(tvDay.context, R.color.black))
                root.setBackgroundResource(R.drawable.bg_item_day_time_lock_selected)
            } else {
                tvDay.setTextColor(ContextCompat.getColor(tvDay.context, R.color.grayC4CDE2))
                root.setBackgroundResource(R.drawable.bg_item_day_time_lock_unselected)
            }
        }
    }
}