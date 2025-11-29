package com.example.myapplication.ui.adapter

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cem.admodule.databinding.ItemNativeAdViewBinding
import com.cem.admodule.manager.CemAdManager
import com.cem.admodule.viewHolder.NativeViewHolder
import com.example.myapplication.R
import com.example.myapplication.data.model.AdmobData
import com.example.myapplication.data.model.Intruder
import com.example.myapplication.databinding.ItemIntruderBinding
import com.example.myapplication.ui.custom.toPx
import com.example.myapplication.utils.Utils
import java.text.SimpleDateFormat
import java.util.Locale

class IntruderAdapter(
    private var onClickEvent: (Intruder) -> Unit = {},
    private var onDeleteClickListener: ((Intruder, Int) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isShowDeleteLocation: Boolean = false
    var intruders = mutableListOf<Any>()
    fun submitList(newValue: List<Any>) {
        intruders.clear()
        intruders.addAll(newValue)
        notifyDataSetChanged()
    }

    fun changeDeleteLocationState() {
        isShowDeleteLocation = !isShowDeleteLocation
        notifyDataSetChanged()
    }

    fun removeLocation(locationLock: Intruder, position: Int) {
        intruders.remove(locationLock)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    inner class IntruderViewHolder(val binding: ItemIntruderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(intruder: Intruder, position: Int) {
            Glide.with(binding.root.context).load(intruder.imageUrl).error(
                ColorDrawable(
                    ContextCompat.getColor(
                        binding.root.context, R.color.grayC4CDE2
                    )
                )
            ).into(binding.imgIntruder)

            binding.tvTriesCount.text = String.format(
                binding.root.context.getString(R.string.number_tries, intruder.tryCount)
            )
            val dateFormat = SimpleDateFormat(Utils.DATE_FORMAT_FULL, Locale.getDefault())
            binding.tvTime.text = dateFormat.format(intruder.time.toLong())
            binding.tvAppName.text = intruder.appName

            binding.imgDelete.setOnClickListener {
                onDeleteClickListener?.invoke(intruder, position)
            }

            if (isShowDeleteLocation) {
                binding.llLocation.animate().translationX((-68f).toPx).start()
            } else {
                binding.llLocation.animate().translationX(0f).start()
            }

            binding.imgDelete.isVisible = isShowDeleteLocation

            if (CemAdManager.getInstance(binding.root.context).isVip()) {
                binding.root.setOnClickListener {
                    onClickEvent.invoke(intruder)
                }
            } else {
                if (position == 1) {
                    binding.root.setOnClickListener {
                        onClickEvent.invoke(intruder)
                    }
                    binding.root.alpha = 1f
                } else {
                    binding.root.alpha = 0.5f
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM -> IntruderViewHolder(
                ItemIntruderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            TYPE_ADS -> NativeViewHolder(
                ItemNativeAdViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            else -> throw IllegalArgumentException("view type valid")
        }
    }

    override fun getItemCount(): Int {
        return intruders.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        intruders.getOrNull(position)?.let { item ->
            when (holder) {
                is IntruderViewHolder -> holder.onBind(item as Intruder, position)
                is NativeViewHolder -> {}
                else -> throw IllegalArgumentException("view type valid")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (intruders[position]) {
            is Intruder -> TYPE_ITEM
            is AdmobData.NativeView -> TYPE_ADS
            else -> throw IllegalArgumentException("view type valid")
        }
    }

    companion object {
        const val TYPE_ITEM = 2
        const val TYPE_ADS = 3
    }
}