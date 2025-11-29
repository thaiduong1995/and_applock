package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.model.CommonSelector
import com.example.myapplication.data.model.UnlockCount
import com.example.myapplication.databinding.ItemCommonSelectorBinding

class CommonSelectorAdapter : RecyclerView.Adapter<CommonSelectorAdapter.ViewHolder>() {

    private var listData = listOf<CommonSelector>()
    var onClickCallback: ((CommonSelector) -> Unit)? = null

    fun setData(data: List<CommonSelector>) {
        this.listData = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(var binding: ItemCommonSelectorBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCommonSelectorBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData.getOrNull(position)
        item?.let {
            holder.binding.apply {
                checkbox.isChecked = it.isSelected
                if (item is UnlockCount) {
                    tvName.text =
                        String.format(root.context.getString(R.string.after_time, it.value))
                } else {
                    tvName.text = it.idString?.let { it1 -> root.context.getString(it1) }
                }
                if (item.isSelected) {
                    root.setBackgroundResource(R.drawable.bg_stroke_1_color_blue_radius_100)
                } else {
                    root.setBackgroundResource(R.drawable.bg_transparent)
                }
                root.setOnClickListener {
                    onClickCallback?.invoke(item)
                    resetSateAllItem()
                    item.isSelected = true
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun resetSateAllItem() {
        listData.forEach {
            it.isSelected = false
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}