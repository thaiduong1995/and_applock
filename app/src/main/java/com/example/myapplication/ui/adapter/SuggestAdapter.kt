package com.example.myapplication.ui.adapter

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.data.model.AppData
import com.example.myapplication.databinding.ItemSuggestAppBinding

class SuggestAdapter(
    private val listApp: ArrayList<AppData> = arrayListOf()
) : BaseAdapter<ItemSuggestAppBinding, AppData>(listApp) {

    var onItemSeclected: ((Boolean) -> Unit)? = null

    override fun binding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean,
        viewType: Int
    ): ItemSuggestAppBinding {
        return ItemSuggestAppBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemSuggestAppBinding, position: Int) {
        listApp.getOrNull(position)?.let { appData ->
            binding.tvAppName.text = appData.appName
            binding.imgSelected.isVisible = appData.selected
            Glide.with(binding.root.context).load("pkg:".plus(appData.packageName))
                .error(
                    ColorDrawable(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.grayA3A3A3
                        )
                    )
                )
                .into(binding.imgIcon)


            binding.root.setOnClickListener {
                appData.selected = !appData.selected
                binding.imgSelected.isVisible = appData.selected
                onItemSeclected?.invoke(!data.filter { it.selected }.isNullOrEmpty())
            }

            onItemSeclected?.invoke(!data.filter { it.selected }.isNullOrEmpty())
        }
    }
}