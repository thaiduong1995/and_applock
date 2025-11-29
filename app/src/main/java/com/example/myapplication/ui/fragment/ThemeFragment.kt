package com.example.myapplication.ui.fragment

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.data.model.OverlayValidateType
import com.example.myapplication.data.model.ThemeData
import com.example.myapplication.data.model.ThemeTopic
import com.example.myapplication.databinding.FragmentThemeBinding
import com.example.myapplication.extention.FirebaseEvent
import com.example.myapplication.ui.adapter.ThemeTopicAdapter
import com.example.myapplication.ui.custom.GridItemDecoration
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.DimensUtil
import com.example.myapplication.utils.Utils
import com.example.myapplication.view_model.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThemeFragment : BaseCacheFragment<FragmentThemeBinding>() {

    private val themeViewModel by activityViewModels<ThemeViewModel>()

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): FragmentThemeBinding {
        return FragmentThemeBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        FirebaseEvent.viewTheme()
        themeViewModel.getTheme()
    }

    override fun initUI() {
        initImageDefault()
        initRecyclerView()
    }

    private fun initImageDefault() {
        themeViewModel.getThemeId.observe(viewLifecycleOwner) {
            val lockType = OverlayValidateType.entries.find {
                it.value == themeViewModel.getLockType()
            } ?: OverlayValidateType.TYPE_PATTERN
            it.getData()?.let { themeId ->
                val background = when (lockType.value) {
                    OverlayValidateType.TYPE_PATTERN.value -> {
                        Utils.getAssetPath(themeId).plus(ThemeData.PREVIEW_PATTERN)
                    }

                    OverlayValidateType.TYPE_KNOCK_CODE.value -> {
                        Utils.getAssetPath(themeId).plus(ThemeData.PREVIEW_KNOCK)
                    }

                    OverlayValidateType.TYPE_PIN.value -> {
                        Utils.getAssetPath(themeId).plus(ThemeData.PREVIEW_PIN)
                    }

                    else -> null
                }
                background?.let { path ->
                    if (themeId != Constants.DEFAULT_THEME) {
                        Glide.with(this).load(Uri.parse(Constants.ASSET_PATH.plus(path)))
                            .error(R.drawable.ic_stardard_theme).apply(
                                RequestOptions().transform(
                                    FitCenter(), RoundedCorners(DimensUtil.dpToPx(20))
                                ).diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate()
                            ).into(binding.imgDefaultTheme)
                    }
                }
                themeViewModel.getCurrentCustomTheme()?.let { item ->
                    if (lockType.value == item.lockType && themeId == Constants.DEFAULT_THEME) {
                        val backgroundPath = item.previewImagePath
                        Glide.with(this).load(backgroundPath).apply(
                            RequestOptions().transform(
                                FitCenter(), RoundedCorners(DimensUtil.dpToPx(20))
                            ).diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate()
                        ).into(binding.imgDefaultTheme)
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        val themeAdapter = ThemeTopicAdapter(ArrayList(ThemeTopic.values().toList()))
        themeAdapter.onClickEvent = {
            findNavController().navigate(
                R.id.topicPreviewScreen, bundleOf(Constants.KEY_THEME_TOPIC to it)
            )
        }
        binding.recycler.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = themeAdapter
        }

        binding.recycler.addItemDecoration(GridItemDecoration(requireContext(), 2))
    }

    override fun initListener() {
        binding.btnCustomTheme.setOnClickListener {
            findNavController().navigate(R.id.customThemeScreen)
        }

        binding.btnMyLibrary.setOnClickListener {
            findNavController().navigate(R.id.myLibraryScreen)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = ThemeFragment().apply {
            arguments = bundleOf()
        }
    }
}