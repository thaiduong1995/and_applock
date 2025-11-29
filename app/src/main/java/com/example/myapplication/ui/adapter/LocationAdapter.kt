package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.data.model.LocationLock
import com.example.myapplication.databinding.ItemLocationBinding
import com.example.myapplication.ui.custom.toPx

class LocationAdapter(
    locations: ArrayList<LocationLock> = arrayListOf(),
    private var onSwitchInverseListener: ((LocationLock) -> Unit)? = null,
    private var onEditClickListener: ((LocationLock) -> Unit)? = null,
    private var onDeleteClickListener: ((LocationLock, Int) -> Unit)? = null
) : BaseAdapter<ItemLocationBinding, LocationLock>(locations) {

    override fun binding(
        inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean, viewType: Int
    ): ItemLocationBinding {
        return ItemLocationBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemLocationBinding, position: Int) {
        data.getOrNull(position)?.let { location ->
            binding.apply {
                tvLocationName.text = location.locationName
                tvLocationAddress.text = location.address
                switchInverse.isChecked = location.enabled
                tvValueRadius.text = "${location.radius}m"
                switchInverse.setOnCheckedChangeListener { button, checked ->
                    location.enabled = checked
                    onSwitchInverseListener?.invoke(location)
                }
                binding.tvEdit.setOnClickListener {
                    onEditClickListener?.invoke(location)
                }
            }
        }

        binding.imgDelete.setOnClickListener {
            onDeleteClickListener?.invoke(data[position], position)
        }

        if (isShowDeleteLocation) {
            binding.llLocation.animate().translationX((-68f).toPx).start()
        } else {
            binding.llLocation.animate().translationX(0f).start()
        }
        binding.imgDelete.isVisible = isShowDeleteLocation
    }

    var isShowDeleteLocation: Boolean = false

    fun changeDeleteLocationState() {
        isShowDeleteLocation = !isShowDeleteLocation
        notifyDataSetChanged()
    }

    fun removeLocation(locationLock: LocationLock, position: Int) {
        data.remove(locationLock)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }
}