package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.model.ItemWifi
import com.example.myapplication.databinding.ItemChooseWifiBinding

class ChooseWifiAdapter(
    private val currentWifi: ItemWifi? = null,
    private val wifis: MutableList<ItemWifi> = mutableListOf()
) : RecyclerView.Adapter<ChooseWifiAdapter.ChooseWifiViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseWifiViewHolder {
        val binding =
            ItemChooseWifiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChooseWifiViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return wifis.size
    }

    override fun onBindViewHolder(holder: ChooseWifiViewHolder, position: Int) {
        holder.bindData(wifis[position], position)
    }

    fun refreshData(wrapItemWifis: List<ItemWifi>) {
        this.wifis.apply {
            clear()
            addAll(wrapItemWifis)
        }
        notifyDataSetChanged()
    }

    inner class ChooseWifiViewHolder(
        private val binding: ItemChooseWifiBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(itemWifi: ItemWifi, position: Int) {
            binding.apply {
                ivWifi.setImageResource(if (itemWifi.bssId == currentWifi?.bssId) R.drawable.ic_wifi_connected else R.drawable.ic_wifi_not_connect)

                tvWifiName.text = itemWifi.ssid

                tvWifiConnected.isVisible = itemWifi.bssId == currentWifi?.bssId

                ivWifiSelected.setImageResource(if (itemWifi.enabled) R.drawable.bg_checkbox_selected else R.drawable.bg_uncheck_clean)

                root.setOnClickListener {
                    itemWifi.enabled = !itemWifi.enabled
                    notifyItemChanged(position)
                }
            }
        }
    }
}

class WrapItemWifi(
    val itemWifi: ItemWifi,
    val isConnected: Boolean
) {
    var isSelected: Boolean = false

    fun changeSelectedState() {
        isSelected = !isSelected
    }
}
