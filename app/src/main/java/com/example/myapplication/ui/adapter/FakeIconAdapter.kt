package com.example.myapplication.ui.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.data.model.AppData
import com.example.myapplication.databinding.ItemFakeIconBinding
import com.example.myapplication.extention.setRadius

class FakeIconAdapter(
    var listApp: ArrayList<AppData>,
    var onAddIcon: (Int, String) -> Unit,
    var onChangeIcon: (AppData) -> Unit
) : BaseAdapter<ItemFakeIconBinding, AppData>(listApp) {

    override fun binding(
        inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean, viewType: Int
    ): ItemFakeIconBinding {
        return ItemFakeIconBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemFakeIconBinding, position: Int) {
        data.getOrNull(position)?.let { appData ->
            Glide.with(binding.root.context).load("pkg:${appData.packageName}")
                .into(binding.imgIcon)

            if (appData.fakeIcon != null) {
                binding.imgAdd.setImageBitmap(appData.fakeIcon)
            } else binding.imgAdd.setImageResource(R.drawable.ic_add_icon)
            binding.imgAdd.setRadius(24)
            binding.imgAdd.setOnClickListener {
                onAddIcon.invoke(position, appData.appName)
            }

            binding.tvChange.setOnClickListener {
                onChangeIcon.invoke(appData)
            }
        }
    }

    fun updateData(currentPosition: Int, icon: Bitmap?) {
        data[currentPosition].fakeIcon = icon
        notifyItemChanged(currentPosition)
    }
}