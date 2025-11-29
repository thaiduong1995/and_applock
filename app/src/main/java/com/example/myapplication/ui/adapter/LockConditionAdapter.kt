package com.example.myapplication.ui.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.data.model.LockCondition
import com.example.myapplication.databinding.ItemLockConditionBinding

class LockConditionAdapter(
    listConditionLock: ArrayList<LockCondition> = arrayListOf()
) : BaseAdapter<ItemLockConditionBinding, LockCondition>(listConditionLock) {

    override fun binding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean,
        viewType: Int
    ): ItemLockConditionBinding {
        return ItemLockConditionBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemLockConditionBinding, position: Int) {
        when (data[position]) {
            LockCondition.TIME_LOCK -> {
                binding.tvName.text = binding.root.context.getString(R.string.time_based_lock)
                binding.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.green73D9AE
                    )
                )
            }

            LockCondition.WIFI_LOCK -> {
                binding.tvName.text = binding.root.context.getString(R.string.wifi_based_lock)
                binding.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.purple80B5FF
                    )
                )
            }

            LockCondition.LOCATION_LOCK -> {
                binding.tvName.text = binding.root.context.getString(R.string.location_based_lock)
                binding.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.yellowFBBA6A
                    )
                )
            }
        }
    }
}