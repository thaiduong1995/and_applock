package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.data.model.Cleaner
import com.example.myapplication.data.model.Contacts
import com.example.myapplication.data.model.Image
import com.example.myapplication.data.model.JunkFile
import com.example.myapplication.data.model.Video
import com.example.myapplication.databinding.ItemCleanerBinding
import com.example.myapplication.databinding.ItemScanBinding
import com.example.myapplication.extention.getSizeString

class CleanerAdapter : BaseAdapter<ItemCleanerBinding, Cleaner>(arrayListOf()) {

    override fun binding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean,
        viewType: Int
    ): ItemCleanerBinding {
        return ItemCleanerBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemCleanerBinding, position: Int) {
        data.getOrNull(position)?.let { cleanerData ->
            binding.tvTitle.text = binding.root.context.getString(cleanerData.resTitle)
            binding.imgIcon.setImageResource(cleanerData.resId)
            binding.imgIconSelected.setImageResource(if (cleanerData.isSelected) R.drawable.bg_checkbox_selected else R.drawable.bg_uncheck_clean)
            binding.imgIconSelected.setOnClickListener {
                cleanerData.isSelected = !cleanerData.isSelected
                binding.imgIconSelected.setImageResource(if (cleanerData.isSelected) R.drawable.bg_checkbox_selected else R.drawable.bg_uncheck_clean)
            }
            when (cleanerData) {
                is JunkFile -> {
                    binding.tvDec.text = binding.root.context.getString(R.string.cache_ad_files_apk)
                }

                is Image -> {
                    val length = cleanerData.listFile.getSizeString()
                    binding.tvDec.text = binding.root.context.getString(
                        R.string.images,
                        cleanerData.listFile.size,
                        length
                    )
                }

                is Video -> {
                    val length = cleanerData.listFile.getSizeString()
                    binding.tvDec.text = binding.root.context.getString(
                        R.string.images,
                        cleanerData.listFile.size,
                        length
                    )
                }

                is Contacts -> {
                    binding.tvDec.text =
                        binding.root.context.getString(R.string.contacts, cleanerData.contacts.size)
                }
            }
        }
    }


    fun setDataClean(data: ArrayList<Cleaner>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun clearData() {
//        val itemSize = data.size
//        for (index in 0 until itemSize) {
//            val reversedIndex = itemSize - index - 1
//            android.os.Handler(Looper.getMainLooper()).postDelayed({
//                data.removeAt(reversedIndex)
//                notifyItemRemoved(reversedIndex)
//            },1000)
//        }
        data.removeAt(3)
        notifyItemRemoved(3)
    }
}

class ScanAdapter :
    BaseAdapter<ItemScanBinding, Cleaner>(arrayListOf()) {

    override fun binding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean,
        viewType: Int
    ): ItemScanBinding {
        return ItemScanBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemScanBinding, position: Int) {
        data.getOrNull(position)?.let { cleanerData ->
            binding.tvTitle.text = binding.root.context.getString(cleanerData.resTitle)
            binding.tvDec.isVisible = cleanerData.cleanCompleted
            binding.imgIconSelected.setImageResource(if (cleanerData.cleanCompleted) R.drawable.ic_checkbox_selected else R.drawable.ic_checkbox_normal)
            if (cleanerData.cleanCompleted) {
                when (cleanerData) {
                    is JunkFile -> {
                        binding.tvDec.text = cleanerData.listJunkFile.getSizeString()
                    }

                    is Image -> {
                        val length = cleanerData.listImageDuplicate.getSizeString()
                        binding.tvDec.text = binding.root.context.getString(
                            R.string.photo_count,
                            cleanerData.listImageDuplicate.size,
                            length
                        )
                    }

                    is Video -> {
                        val length = cleanerData.listVideoDuplicate.getSizeString()
                        binding.tvDec.text = binding.root.context.getString(
                            R.string.video_count,
                            cleanerData.listVideoDuplicate.size,
                            length
                        )
                    }

                    is Contacts -> {
                        binding.tvDec.text =
                            binding.root.context.getString(
                                R.string.contacts,
                                cleanerData.listContactDuplicate.size
                            )
                    }
                }
            }
        }
    }

    fun setDataClean(data: ArrayList<Cleaner>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun resetState() {
        data.onEach { it.cleanCompleted = false }
        notifyDataSetChanged()
    }

    fun removeItem(index: Int) {
        notifyItemRemoved(index)
    }
}

class CustomItemAnimator : DefaultItemAnimator() {
    override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
        holder?.let {
            holder.itemView.animate().translationX(-holder.itemView.measuredWidth.toFloat()).start()
        }
        return true
    }
}