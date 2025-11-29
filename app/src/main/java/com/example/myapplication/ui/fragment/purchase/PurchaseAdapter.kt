package com.example.myapplication.ui.fragment.purchase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemPurchaseBinding
import com.example.myapplication.extention.gone
import com.example.myapplication.extention.visible

class PurchaseAdapter : RecyclerView.Adapter<PurchaseAdapter.ViewHolder>() {

    var listData = mutableListOf<PurchaseModel>()
    var positionCurrent: Int = 1

    fun setData(newValue: List<PurchaseModel>) {
        listData.clear()
        listData.addAll(newValue)
        notifyDataSetChanged()
    }

    fun setPosition(position: Int) {
        positionCurrent = position
        notifyItemChanged(position, 0)
    }

    inner class ViewHolder(val binding: ItemPurchaseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: PurchaseModel) {
            binding.txtName.text = binding.root.context.getString(item.idName)
            binding.txtPrice.text = item.price
            binding.txtPriceMonth.text = item.priceMonth
            item.description?.let {
                binding.txtDes.text = binding.root.context.getString(it)
            }
            binding.txtDes.isVisible = item.description != null
            when (item) {
                is PurchaseModel.PurchaseYearModel -> {
                    binding.txtRecommend.gone()
                }

                is PurchaseModel.PurchaseMonthModel -> {
                    binding.txtRecommend.gone()
                }

                is PurchaseModel.PurchaseWeekModel -> {
                    binding.txtRecommend.visible()
                }
            }
            binding.cardView.setBackgroundResource(
                if (positionCurrent == layoutPosition) R.drawable.bg_selected_purchase
                else R.drawable.bg_unselected_purchase
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPurchaseBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        listData.getOrNull(position)?.let {
            holder.onBind(it)
        }
    }
}