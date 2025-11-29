package com.example.myapplication.ui.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.data.model.CustomTheme
import com.example.myapplication.databinding.FragmentMyLibraryBinding
import com.example.myapplication.ui.adapter.ImageAdapter
import com.example.myapplication.ui.adapter.ImageType
import com.example.myapplication.ui.custom.LinearItemDecoration
import com.example.myapplication.ui.custom.toPx
import com.example.myapplication.ui.dialog.DialogConfirmToDelete
import com.example.myapplication.utils.Constants
import com.example.myapplication.view_model.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyLibraryFragment : BaseCacheFragment<FragmentMyLibraryBinding>() {

    private val viewModel by activityViewModels<ThemeViewModel>()
    private val adapter = ImageAdapter(arrayListOf())
    private val listCustomTheme = ArrayList<CustomTheme>()

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMyLibraryBinding {
        return FragmentMyLibraryBinding.inflate(inflater, container, false)
    }

    override fun initListener() {
        binding.imgBack.setOnClickListener {
            popBackStack()
        }

        binding.tvDelete.setOnClickListener {
            binding.tvDelete.visibility = View.GONE
            binding.tvCancel.visibility = View.VISIBLE
            adapter.showButtonDelete(true)
        }

        binding.tvCancel.setOnClickListener {
            binding.tvDelete.visibility = View.VISIBLE
            binding.tvCancel.visibility = View.GONE
            adapter.showButtonDelete(false)
        }

        binding.btnCustomTheme.setOnClickListener {
            findNavController().navigate(R.id.customThemeScreen)
        }
    }

    override fun initObservers() {
        viewModel.customThemeLiveData?.observe(this) {
            if (it != null) {
                binding.progressBar.visibility = View.GONE
                listCustomTheme.clear()
                listCustomTheme.addAll(it)
                adapter.setData(ArrayList(it.map { it.previewImagePath }))
            }
        }
    }

    override fun initUI() {
        adapter.setImageTypeLoading(ImageType.FILE)
        adapter.setImageSize(resources.displayMetrics.widthPixels / 2)
        binding.recycler.layoutManager = GridLayoutManager(context, 2)
        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(
            LinearItemDecoration(
                4f.toPx.toInt(),
                4f.toPx.toInt()
            )
        )
        adapter.onClickEvent = { imagePath, position ->
            val bundle = bundleOf(
                Constants.KEY_THEME_PREVIEW to listCustomTheme,
                Constants.KEY_POSITION to position
            )
            findNavController().navigate(R.id.previewListCustomThemeScreen, bundle)
        }

        adapter.onClickDelete = { imagePath, position ->
            val currentTheme = listCustomTheme.getOrNull(position)
            currentTheme?.let {
                showDialogDelete(it)
            }
        }
    }

    private fun showDialogDelete(customTheme: CustomTheme) {
        val dialog = DialogConfirmToDelete.newInstance(getString(R.string.confirm_delete_theme))
        if (!dialog.isShown) {
            dialog.show(childFragmentManager, "")
        }
        dialog.callback = {
            viewModel.deleteCustomTheme(customTheme)
            viewModel.getTheme()
        }
    }

    override fun loadAds() {
        activity?.let { act ->
            CemAdManager.getInstance(act).loadBannerAndShowByActivity(
                activity = act,
                viewGroup = binding.layoutBanner.bannerLayout,
                configKey = ConstAd.BANNER_KEY_HOME,
                position = ConstAd.POS_BOTTOM_BANNER,
                nameScreen = this::class.simpleName,
                callback = object : BannerAdListener {
                    override fun onBannerLoaded(banner: BannerAdView, view: View) {}

                    override fun onBannerFailed(error: String?) {
                        Log.d("onBannerFailed", "$error")
                    }

                    override fun onBannerClicked() {}

                    override fun onBannerOpen() {}

                    override fun onBannerClose() {}

                }
            )
        }
    }
}