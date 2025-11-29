package com.example.myapplication.ui.fragment.preview

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.databinding.FragmentPreviewOneBinding

class PreviewOneFragment : BaseCacheFragment<FragmentPreviewOneBinding>() {
    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPreviewOneBinding {
        return FragmentPreviewOneBinding.inflate(inflater, container, false)
    }
}