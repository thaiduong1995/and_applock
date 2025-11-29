package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.model.GroupWifi
import com.example.myapplication.databinding.ItemWifiBinding
import com.example.myapplication.ui.custom.toPx

class WifiAdapter(
    private val groupWifis: MutableList<GroupWifi> = mutableListOf(),
    private val onEnableWifi: (GroupWifi) -> Unit = {},
    private val onEditWifi: (GroupWifi) -> Unit = {},
    private val onRemoteWifi: ((GroupWifi, position: Int) -> Unit)? = null
) : RecyclerView.Adapter<WifiAdapter.WifiViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiViewHolder {
        val binding = ItemWifiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WifiViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return groupWifis.size
    }

    override fun onBindViewHolder(holder: WifiViewHolder, position: Int) {
        holder.bindData(groupWifis[position], position)
    }

    var isShowDeleteWifi: Boolean = false

    fun changeDeleteWifiState() {
        isShowDeleteWifi = !isShowDeleteWifi
        notifyDataSetChanged()
    }

    fun refreshData(newGroupWifis: List<GroupWifi>) {
        this.groupWifis.apply {
            clear()
            addAll(newGroupWifis)
        }
        notifyDataSetChanged()
    }

    fun removeWifi(groupWifi: GroupWifi, position: Int) {
        groupWifis.remove(groupWifi)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    inner class WifiViewHolder(
        private val binding: ItemWifiBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(groupWifi: GroupWifi, position: Int) {
            binding.apply {
                tvWifiName.text = "Group Wi-Fi $position (${groupWifi.childWifiCount})"

                swcEnableWifi.apply {
                    isChecked = groupWifi.enabled
                    setOnCheckedChangeListener { _, isChecked ->
                        groupWifi.enabled = isChecked
                        onEnableWifi(groupWifi)
                    }
                }

                tvEdit.setOnClickListener {
                    onEditWifi(groupWifi)
                }
                if (isShowDeleteWifi) {
                    binding.layoutWifi.animate().translationX((-68f).toPx).start()
                } else {
                    binding.layoutWifi.animate().translationX(0f).start()
                }

                ivRemoveWifi.apply {
                    setOnClickListener {
                        onRemoteWifi?.invoke(groupWifi, position)
                    }
                    isVisible = isShowDeleteWifi
                }
            }
        }
    }
}
