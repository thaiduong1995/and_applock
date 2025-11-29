package com.example.myapplication.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.model.RecentSearch
import com.example.myapplication.databinding.ItemRecentSearchBinding
import com.example.myapplication.extention.setOnSingleClickListener

class RecentSearchLocationAdapter(
    val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mData: MutableList<RecentSearch> = mutableListOf()

    var onLocationClicked: ((RecentSearch) -> Unit)? = null

    fun setData(data: MutableList<RecentSearch>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemRecentSearchBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindData(position)
    }

    inner class MyViewHolder(val binding: ItemRecentSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            val recentSearch = mData[position]
            binding.apply {
                tvAddressName.text = recentSearch.addressName
                tvRoad.text = recentSearch.road
                imgRecent.setImageResource(if (recentSearch.type == 0) R.drawable.ic_time else R.drawable.ic_recent_location)
                root.setOnSingleClickListener {
                    onLocationClicked?.invoke(recentSearch)
                }
            }
        }
    }
}