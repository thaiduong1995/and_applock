package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.data.model.Password
import com.example.myapplication.databinding.ItemTypePasswordBinding

class TypePasswordAdapter(
    data: ArrayList<Password> = arrayListOf(),
    private val onClickEvent: (Password) -> Unit = {}
) : BaseAdapter<ItemTypePasswordBinding, Password>(data) {

    override fun binding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean,
        viewType: Int
    ): ItemTypePasswordBinding {
        return ItemTypePasswordBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemTypePasswordBinding, position: Int) {
        data.getOrNull(position)?.let { typePassword ->
            if (typePassword.selected) {
                binding.root.setBackgroundResource(R.drawable.bg_stroke_1_color_blue_radius_100)
            } else {
                binding.root.setBackgroundResource(R.drawable.bg_transparent)
            }
            binding.imgSelect.setImageResource(if (typePassword.selected) R.drawable.ic_checkbox_selected else R.drawable.ic_checkbox_normal)
            binding.tvName.text = typePassword.name
            binding.imgIcon.setImageResource(typePassword.icon)
            binding.root.setOnClickListener {
                data.onEach {
                    it.selected = false
                }
                typePassword.selected = true
                onClickEvent.invoke(typePassword)
                notifyDataSetChanged()
            }
        }
    }
}