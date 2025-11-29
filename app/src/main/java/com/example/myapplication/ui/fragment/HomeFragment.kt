package com.example.myapplication.ui.fragment

import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.data.model.Tools
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.extention.FirebaseEvent
import com.example.myapplication.extention.addOnGlobalLayoutCallback
import com.example.myapplication.extention.addOnPageChangeCallback
import com.example.myapplication.ui.adapter.CommonPagerAdapter
import com.example.myapplication.ui.adapter.ToolsAdapter
import com.example.myapplication.utils.Constants
import com.example.myapplication.view_model.MainViewModel

/**
 * Created by Thinhvh on 22/09/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
class HomeFragment : BaseCacheFragment<FragmentHomeBinding>() {

    private val mainViewModel by activityViewModels<MainViewModel>()
    private var toolsAdapter: ToolsAdapter? = null
    private val listFragment = mutableListOf<Fragment>()
    private var layoutToolsTranslateY = 0f

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        listFragment.apply {
            clear()
            addAll(
                listOf(
                    LockFragment.newInstance(),
                    SecurityFragment.newInstance(),
                    ThemeFragment.newInstance(),
                    SettingFragment.newInstance()
                )
            )
        }
    }

    override fun initUI() {
        initViewPager()
        initLayoutTools()
    }

    private fun initViewPager() {
        val pagerAdapter = CommonPagerAdapter(childFragmentManager, lifecycle)
        pagerAdapter.setListFragment(listFragment)
        binding.viewPager.apply {
            adapter = pagerAdapter
            isSaveEnabled = false
            isUserInputEnabled = false
            offscreenPageLimit = pagerAdapter.itemCount
            addOnPageChangeCallback { hideLayoutTools() }
        }
        binding.bottomNavView.setupWithViewpager(binding.viewPager)

        mainViewModel.regexScreenLiveData.observe(this) {
            when (it?.type) {
                Constants.REGEX_TYPE_LOCK -> { }

                Constants.REGEX_TYPE_THEME -> {
                    binding.viewPager.setCurrentItem(2, false)
                }

                Constants.REGEX_TYPE_WEB -> {
                    FirebaseEvent.viewBrowser()
                    findNavController().navigate(R.id.webBrowserScreen)
                }

                else -> {}
            }
        }
    }


    private fun initLayoutTools() {
        toolsAdapter = ToolsAdapter(ArrayList(Tools.entries)) {
            onToolsItemClick(it)
        }
        binding.layoutTools.apply {
            recycler.layoutManager =
                GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
            recycler.adapter = toolsAdapter
            root.addOnGlobalLayoutCallback {
                val layoutParams =
                    binding.layoutTools.root.layoutParams as ViewGroup.MarginLayoutParams
                val height = resources.displayMetrics.heightPixels / 2
                binding.layoutTools.root.layoutParams = layoutParams
                binding.layoutTools.root.translationY = height.toFloat()
                layoutToolsTranslateY = height.toFloat()
            }
        }

    }

    private fun onToolsItemClick(tools: Tools) {
        when (tools) {
            Tools.FAKE_APP_ICONS -> {
                FirebaseEvent.viewFakeIcons()
                findNavController().navigate(R.id.fakeIconScreen)
            }

            Tools.FAKE_COVER -> {
                FirebaseEvent.viewFakeCover(FirebaseEvent.VIEW_FAKE_COVER)
                findNavController().navigate(R.id.fakeCoverScreen)
            }

            Tools.CAPTURE_INTRUDER -> {
                FirebaseEvent.viewCaptureIntruders()
                findNavController().navigate(R.id.intrudersScreen)
            }

            Tools.WEB_BROWSER -> {
                FirebaseEvent.viewBrowser()
                findNavController().navigate(R.id.webBrowserScreen)
            }
        }
    }

    override fun initListener() {
        binding.apply {
            bottomNavView.onClickTool = {
                onClickToolListener()
                if (binding.layoutTools.root.translationY == 0f) {
                    hideLayoutTools()
                } else {
                    showLayoutTools()
                }
            }
            layoutTools.root.setOnClickListener { }
        }
    }

    private fun onClickToolListener() {
        loadBanner()
    }

    private fun showLayoutTools() {
        FirebaseEvent.viewTools()
        binding.layoutTools.root.animate().translationY(0f)
    }

    private fun hideLayoutTools() {
        if (layoutToolsTranslateY != 0f) {
            binding.layoutTools.root.animate().translationY(layoutToolsTranslateY)
        }
    }

    override fun onStart() {
        super.onStart()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun loadAds() {
        loadBanner()
    }

    private fun loadBanner() {
        activity?.let { act ->
            CemAdManager.getInstance(act).loadBannerAndShowByActivity(
                activity = act,
                viewGroup = binding.layoutBanner.bannerLayout,
                configKey = ConstAd.BANNER_KEY_HOME,
                position = ConstAd.POS_BOTTOM_BANNER,
                nameScreen = this::class.simpleName,
                callback = object : BannerAdListener {
                    override fun onBannerLoaded(banner: BannerAdView, view: View) { }

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

    override fun onDestroy() {
        super.onDestroy()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun dispatchTouchEvent(event: MotionEvent) {
        val layoutToolRect = Rect()
        val bottomBarRect = Rect()
        binding.layoutTools.root.getGlobalVisibleRect(layoutToolRect)
        binding.bottomNavView.getGlobalVisibleRect(bottomBarRect)
        if (!layoutToolRect.contains(
                event.rawX.toInt(), event.rawY.toInt()
            ) && !bottomBarRect.contains(event.rawX.toInt(), event.rawY.toInt())
        ) {
            hideLayoutTools()
        }
    }
}