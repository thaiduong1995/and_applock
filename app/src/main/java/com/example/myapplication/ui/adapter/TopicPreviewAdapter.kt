package com.example.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseAdapter
import com.example.myapplication.data.model.LockType
import com.example.myapplication.data.model.ThemePreview
import com.example.myapplication.databinding.ItemPreviewPasswordTypeBinding
import com.example.myapplication.ui.custom.LinearItemDecoration
import com.example.myapplication.ui.custom.toPx

class TopicPreviewAdapter(
    listTheme: ArrayList<ThemePreview> = arrayListOf()
) : BaseAdapter<ItemPreviewPasswordTypeBinding, ThemePreview>(listTheme) {

    private var imageSize = 0
    var onItemClick: ((ThemePreview, LockType, Int) -> Unit)? = null
    var onSeeMoreClick: (LockType) -> Unit = {}

    override fun binding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToParent: Boolean,
        viewType: Int
    ): ItemPreviewPasswordTypeBinding {
        return ItemPreviewPasswordTypeBinding.inflate(inflater, parent, attachToParent)
    }

    override fun onBindData(binding: ItemPreviewPasswordTypeBinding, position: Int) {
        binding.recycler.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
        val adapter = PreviewThemeAdapter(data)
        adapter.onClickEvent = { themePreview, previewType, position ->
            onItemClick?.invoke(themePreview, previewType, position)
        }
        adapter.setImageSize(imageSize)
        var type = LockType.PASS_CODE
        when (position) {
            0 -> {
                binding.tvName.text = binding.root.context.getString(R.string.pass_code)
                adapter.setItemType(LockType.PASS_CODE)
                type = LockType.PASS_CODE
            }

            1 -> {
                binding.tvName.text = binding.root.context.getString(R.string.pattern)
                adapter.setItemType(LockType.PATTERN)
                type = LockType.PATTERN
            }

            2 -> {
                binding.tvName.text = binding.root.context.getString(R.string.knock_code)
                adapter.setItemType(LockType.KNOCK)
                type = LockType.KNOCK
            }
        }
        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(/* decor = */ LinearItemDecoration(4f.toPx.toInt(), 0))
        binding.tvSeeMore.setOnClickListener {
            onSeeMoreClick(type)
        }
    }

    override fun getItemCount(): Int {
        return 3
    }

    fun setImageSize(imageSize: Int) {
        this.imageSize = imageSize
    }
}