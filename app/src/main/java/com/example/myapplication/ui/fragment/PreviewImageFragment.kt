package com.example.myapplication.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.databinding.FragmentPreviewImageBinding
import com.example.myapplication.utils.Constants

class PreviewImageFragment : BaseCacheFragment<FragmentPreviewImageBinding>() {

    private var imagePath: String? = null
    private var fromAsset: Boolean = true

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPreviewImageBinding {
        return FragmentPreviewImageBinding.inflate(inflater, container, false)
    }

    override fun getDataBundle() {
        arguments?.let {
            imagePath = it.getString(BUNDLE_IMAGE_PATH)
            fromAsset = it.getBoolean(BUNDLE_FROM_ASSET)
        }
    }

    override fun initUI() {
        context?.let { ct ->
            binding.apply {
                if (fromAsset) {
                    Glide.with(ct).load(Uri.parse(Constants.ASSET_PATH.plus(imagePath))).into(img)
                } else {
                    Glide.with(ct).load(imagePath).into(img)
                }
            }
        }
    }

    companion object {
        private const val BUNDLE_IMAGE_PATH = "param1"
        private const val BUNDLE_FROM_ASSET = "FROM_ASSET"

        @JvmStatic
        fun newInstance(imagePath: String, fromAsst: Boolean = true) =
            PreviewImageFragment().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_IMAGE_PATH, imagePath)
                    putBoolean(BUNDLE_FROM_ASSET, fromAsst)
                }
            }
    }
}