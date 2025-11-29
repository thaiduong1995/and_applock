package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.myapplication.R
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.data.model.DayItem
import com.example.myapplication.data.model.TimeItem
import com.example.myapplication.databinding.ItemDayOfWeekInTimeLockBinding
import com.example.myapplication.databinding.ItemTimeLockBinding
import com.example.myapplication.ui.custom.toPx

class TimeLockAdapter(
    listTimeItems: ArrayList<TimeItem> = arrayListOf(),
    var onEditClick: ((TimeItem, Int) -> Unit)? = null,
    var onDeleteClick: ((TimeItem) -> Unit)? = null,
    var onSwitchChanged: ((TimeItem) -> Unit)? = null
) : BaseAdapter<ItemTimeLockBinding, TimeItem>(listTimeItems) {

    var listDays = arrayListOf(
        DayItem(name = "Mon"),
        DayItem(name = "Tue"),
        DayItem(name = "Wed"),
        DayItem(name = "Thu"),
        DayItem(name = "Fri"),
        DayItem(name = "Sat"),
        DayItem(name = "Sun"),
    )

    override fun binding(
        inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean, viewType: Int
    ): ItemTimeLockBinding {
        return ItemTimeLockBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemTimeLockBinding, position: Int) {
        binding.apply {
            val item = data[position]
            tvTitle.text = item.name
            tvSubtitle.text = String.format(
                tvSubtitle.context.getString(R.string.text_time_lock, item.startTime, item.endTime)
            )
            tvEdit.setOnClickListener {
                onEditClick?.invoke(item, position)
            }
            ivRemove.setOnClickListener {
                onDeleteClick?.invoke(item)
            }
            listDay.removeAllViews()
            listDays.forEachIndexed { _, dayItem ->
                val viewDay = ItemDayOfWeekInTimeLockBinding.inflate(
                    LayoutInflater.from(listDay.context), root, true
                )
                viewDay.apply {
                    tvDay.text = dayItem.name.first().toString()
                    if (item.day.contains(dayItem.name)) {
                        tvDay.setTextColor(ContextCompat.getColor(tvDay.context, R.color.black))
                        root.setBackgroundResource(R.drawable.bg_item_day_time_lock_selected)
                    } else {
                        tvDay.setTextColor(
                            ContextCompat.getColor(
                                tvDay.context, R.color.grayC4CDE2
                            )
                        )
                        root.setBackgroundResource(R.drawable.bg_item_day_time_lock_unselected)
                    }
                }
                if (viewDay.root.parent != null) {
                    (viewDay.root.parent as ViewGroup).removeView(viewDay.root) // <- fix
                }
                listDay.addView(viewDay.root)
            }

            if (isShowDeleteWifi) {
                binding.layoutItemTime.animate().translationX((-68f).toPx).start()
            } else {
                binding.layoutItemTime.animate().translationX(0f).start()
            }
            binding.ivRemove.isVisible = isShowDeleteWifi

            binding.swcEnableWifi.isChecked = item.enable
            binding.swcEnableWifi.setOnCheckedChangeListener { button, checked ->
                item.enable = checked
                onSwitchChanged?.invoke(item)
            }
        }
    }


    var isShowDeleteWifi: Boolean = false

    fun changeDeleteState() {
        isShowDeleteWifi = !isShowDeleteWifi
        notifyDataSetChanged()
    }
}