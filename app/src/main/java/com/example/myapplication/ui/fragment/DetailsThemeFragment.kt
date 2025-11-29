package com.example.myapplication.ui.fragment

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.data.model.LockType
import com.example.myapplication.data.model.ThemeData
import com.example.myapplication.data.model.ThemePreview
import com.example.myapplication.databinding.FragmentDetailsThemeBinding
import com.example.myapplication.extention.addOnPageChangeCallback
import com.example.myapplication.extention.parcelableArrayList
import com.example.myapplication.extention.toastMessageShortTime
import com.example.myapplication.ui.adapter.CommonPagerAdapter
import com.example.myapplication.ui.dialog.DialogConfirmReward
import com.example.myapplication.utils.Constants
import com.example.myapplication.view_model.ThemeViewModel

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsThemeFragment : BaseCacheFragment<FragmentDetailsThemeBinding>() {

    private val viewModel by activityViewModels<ThemeViewModel>()
    private val themePreview: ArrayList<ThemePreview> = arrayListOf()
    private var lockType: LockType? = LockType.PASS_CODE
    private var currentTheme: ThemePreview? = null
    private var position = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.window?.apply {
            when {
                Build.VERSION.SDK_INT in 21..29 -> {
                    setFlags(
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    )
                }

                Build.VERSION.SDK_INT >= 30 -> {
                    val windowInsetsController =
                        WindowCompat.getInsetsController(this, this.decorView)
                    // Configure the behavior of the hidden system bars.
                    windowInsetsController.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

                    // Add a listener to update the behavior of the toggle fullscreen button when
                    // the system bars are hidden or revealed.
                    decorView.setOnApplyWindowInsetsListener { view, windowInsets ->
                        // You can hide the caption bar even when the other system bars are visible.
                        // To account for this, explicitly check the visibility of navigationBars()
                        // and statusBars() rather than checking the visibility of systemBars().
                        if (windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars())
                            || windowInsets.isVisible(WindowInsetsCompat.Type.statusBars())) {
                            // Hide both the status bar and the navigation bar.
                            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                        }
                        view.onApplyWindowInsets(windowInsets)
                    }
                }
            }
        }
    }

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentDetailsThemeBinding {
        return FragmentDetailsThemeBinding.inflate(inflater, container, false)
    }

    override fun getDataBundle() {
        themePreview.apply {
            clear()
            arguments?.parcelableArrayList<ThemePreview>(Constants.KEY_THEME_PREVIEW)
                ?.let { addAll(it) }
        }
        lockType = arguments?.getSerializable(Constants.KEY_THEME_PREVIEW_TYPE) as? LockType
        position = arguments?.getInt(Constants.KEY_POSITION) ?: 0
    }

    override fun initUI() {
        themePreview.let { listThemePreview ->
            val listFragment = mutableListOf<Fragment>()
            when (lockType) {

                LockType.PASS_CODE -> {
                    listThemePreview.flatMap { it.image }.filter {
                        it.contains(ThemeData.PREVIEW_PIN)
                    }.onEach {
                        listFragment.add(PreviewImageFragment.newInstance(it))
                    }
                }

                LockType.PATTERN -> {
                    listThemePreview.flatMap { it.image }.filter {
                        it.contains(ThemeData.PREVIEW_PATTERN)
                    }.onEach {
                        listFragment.add(PreviewImageFragment.newInstance(it))
                    }
                }

                LockType.KNOCK -> {
                    listThemePreview.flatMap { it.image }.filter {
                        it.contains(ThemeData.PREVIEW_KNOCK)
                    }.onEach {
                        listFragment.add(PreviewImageFragment.newInstance(it))
                    }
                }

                else -> {}
            }

            val adapter = CommonPagerAdapter(childFragmentManager, lifecycle)
            adapter.setListFragment(listFragment)
            binding.viewPager.apply {
                this.adapter = adapter
                isSaveEnabled = false
                offscreenPageLimit = listFragment.size
                addOnPageChangeCallback { position ->
                    currentTheme = themePreview.getOrNull(position)
                }
            }

            binding.viewPager.post {
                binding.viewPager.setCurrentItem(position, false)
            }
        }
    }

    override fun initListener() {
        binding.imgBack.setOnClickListener {
            popBackStack()
        }

        binding.tvSave.setOnClickListener {
            val currentPosition = themePreview .indexOf(currentTheme)
            val isCheckThemeVip =
                currentPosition != 0 && !CemAdManager.getInstance(it.context).isVip()
            if (isCheckThemeVip) {
                val dialog = DialogConfirmReward.newInstance(
                    title = getString(R.string.watch_a_video_to_get_theme)
                )
                if (!dialog.isShown) {
                    dialog.show(childFragmentManager, "")
                }
                dialog.callback = { ok ->
                    if (ok) setTheme() else findNavController().navigate(R.id.purchaseScreen)
                }
            } else {
                setTheme()
            }
        }
    }

    private fun setTheme() {
        currentTheme?.let {
            context?.toastMessageShortTime(getString(R.string.change_theme_success))
            viewModel.saveTheme(it)
            viewModel.getTheme()
            popBackStack()
        }
    }

    override fun loadAds() {
        activity?.let { act ->
            CemAdManager.getInstance(act).loadBannerAndShowByActivity(
                activity = act,
                viewGroup = binding.layoutBanner.bannerLayout,
                configKey = ConstAd.BANNER_KEY_DETAIL,
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

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.apply {
            when {
                Build.VERSION.SDK_INT in 21..29 -> {
                    clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                }

                Build.VERSION.SDK_INT >= 30 -> {
                    val windowInsetsController =
                        WindowCompat.getInsetsController(this, this.decorView)
                    // Configure the behavior of the hidden system bars.
                    windowInsetsController.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

                    // Add a listener to update the behavior of the toggle fullscreen button when
                    // the system bars are hidden or revealed.
                    decorView.setOnApplyWindowInsetsListener { view, windowInsets ->
                        // You can hide the caption bar even when the other system bars are visible.
                        // To account for this, explicitly check the visibility of navigationBars()
                        // and statusBars() rather than checking the visibility of systemBars().
                        if (!(windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars())
                            || windowInsets.isVisible(WindowInsetsCompat.Type.statusBars()))) {
                            // Show both the status bar and the navigation bar.
                            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
                        }
                        view.onApplyWindowInsets(windowInsets)
                    }
                }
            }
        }
    }
}