package com.example.myapplication.ui.fragment.preview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.databinding.FragmentPreviewThreeBinding

class PreviewThreeFragment : BaseCacheFragment<FragmentPreviewThreeBinding>() {
    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPreviewThreeBinding {
        return FragmentPreviewThreeBinding.inflate(inflater, container, false)
    }

    override fun initListener() {
        binding.btnGetStarted.setOnClickListener {
            findNavController().popBackStack(R.id.previewScreen, true)
            findNavController().navigate(R.id.setupScreen)
        }
    }
}