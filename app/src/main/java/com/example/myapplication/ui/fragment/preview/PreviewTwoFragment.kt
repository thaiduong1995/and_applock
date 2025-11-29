package com.example.myapplication.ui.fragment.preview

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.databinding.FragmentPreviewTwoBinding

class PreviewTwoFragment : BaseCacheFragment<FragmentPreviewTwoBinding>() {
    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPreviewTwoBinding {
        return FragmentPreviewTwoBinding.inflate(inflater, container, false)
    }
}