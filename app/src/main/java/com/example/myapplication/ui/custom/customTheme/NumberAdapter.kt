package com.example.myapplication.ui.custom.customTheme

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.databinding.ItemNumberBinding

class NumberAdapter(
    listNumber: ArrayList<String> = arrayListOf()
) : BaseAdapter<ItemNumberBinding, String>(listNumber) {

    private var numberColor: Int? = null

    override fun binding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean,
        viewType: Int
    ): ItemNumberBinding {
        return ItemNumberBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemNumberBinding, position: Int) {
        data.getOrNull(position)?.let {
            binding.backgroundView.setImageDrawable(null)
            if (it == "delete") {
                binding.backgroundView.setImageResource(R.drawable.ic_number_delete)
            } else if (it == "refresh") {
                binding.backgroundView.setImageResource(R.drawable.ic_number_refresh)
            } else {
                binding.tvNumber.text = it
            }
            if (numberColor != null) {
                binding.cardImage.setCardBackgroundColor(numberColor!!)
            }
        }
    }

    fun setNumberColor(color: Int) {
        numberColor = color
        notifyDataSetChanged()
    }
}