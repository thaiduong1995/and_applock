package com.example.myapplication.ui.adapter

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.cem.admodule.databinding.ItemNativeAdViewBinding
import com.cem.admodule.viewHolder.NativeViewHolder
import com.example.myapplication.R
import com.example.myapplication.data.model.AdmobData
import com.example.myapplication.data.model.AppData
import com.example.myapplication.data.model.LockCondition
import com.example.myapplication.databinding.ItemLockApplicationBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class InstalledAppAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data = arrayListOf<Any>()
    private val listConditionLock = ArrayList<LockCondition>()
    var onLockStateChanged: ((Int, AppData) -> Unit)? = null

    fun setData(data: ArrayList<Any>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun setLockConditions(lockConditions: ArrayList<LockCondition>) {
        this.listConditionLock.clear()
        this.listConditionLock.addAll(lockConditions)
        notifyDataSetChanged()
    }

    fun updateItem(postition: Int, appData: AppData) {
        data[postition] = appData
        notifyItemChanged(postition)
    }

    class ItemViewHolder(val binding: ItemLockApplicationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM -> ItemViewHolder(
                ItemLockApplicationBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            TYPE_ADS -> NativeViewHolder(
                ItemNativeAdViewBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            else -> throw IllegalArgumentException("view type valid")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        data.getOrNull(position)?.let { appData ->
            when (holder) {
                is ItemViewHolder -> {
                    (appData as AppData).let {
                        Glide.with(holder.binding.root.context)
                            .load("pkg:".plus(appData.packageName)).error(
                                ColorDrawable(
                                    ContextCompat.getColor(
                                        holder.binding.root.context, R.color.grayA3A3A3
                                    )
                                )
                            ).into(holder.binding.imgIcon)
                        holder.binding.tvAppName.text = appData.appName
                        holder.binding.imgLock.setImageResource(if (appData.isLock) R.drawable.ic_lock_selected else R.drawable.ic_lock_normal)
                        holder.binding.root.setOnClickListener {
                            onLockStateChanged?.invoke(position, appData)
                        }
//                        if (appData.isLock && listConditionLock.isNotEmpty()) {
//                            val layoutManager = FlexboxLayoutManager(holder.binding.root.context)
//                            layoutManager.flexDirection = FlexDirection.ROW
//                            layoutManager.justifyContent = JustifyContent.FLEX_START
//                            holder.binding.recycler.layoutManager = layoutManager
//                            holder.binding.recycler.adapter =
//                                LockConditionAdapter(listConditionLock)
//                            holder.binding.recycler.visibility = View.VISIBLE
//                        } else {
                        holder.binding.recycler.visibility = View.GONE
//                        }
                    }
                }

                is NativeViewHolder -> {

                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is AppData -> TYPE_ITEM
            is AdmobData.NativeView -> TYPE_ADS
            else -> throw IllegalArgumentException("view type valid")
        }
    }


    companion object {
        const val TYPE_ITEM = 2
        const val TYPE_ADS = 3
    }
}