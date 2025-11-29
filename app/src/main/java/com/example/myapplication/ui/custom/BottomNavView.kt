package com.example.myapplication.ui.custom

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.R
import com.example.myapplication.databinding.LayoutBottomNavBinding
import com.example.myapplication.extention.addOnPageChangeCallback
import com.example.myapplication.extention.setOnSingleClickListener

class BottomNavView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    styleDef: Int = 0
) : ConstraintLayout(context, attrs, styleDef) {

    private var binding: LayoutBottomNavBinding =
        LayoutBottomNavBinding.inflate(LayoutInflater.from(context), this, true)
    private var listTab = mutableListOf<TabView>()
    var onClickTool: () -> Unit = {}

    fun setupWithViewpager(viewpager: ViewPager2) {
        viewpager.addOnPageChangeCallback { position ->
            listTab.onEach { it.isSelected = false }
            listTab[position].isSelected = true
        }

        listTab.onEach {
            it.setOnClickListener {
                (context as? Activity)?.let { ct ->
                    CemAdManager.getInstance(context).showInterstitialCallback(
                        activity = ct,
                        configKey = ConstAd.FULL_KEY_DETAIL,
                        nameScreen = it::class.simpleName,
                        callback = {
                            val tabIndex = listTab.indexOfFirst { tab -> it == tab }
                            if (tabIndex in 0 until listTab.size) {
                                viewpager.setCurrentItem(tabIndex, false)
                            }
                        }
                    )
                }
//                AdsFullManager.showInterstitial(
//                    context = activity,
//                    isShowRemote = RemoteManager.interSwitch,
//                    onDismiss = {
//                val tabIndex = listTab.indexOfFirst { tab -> it == tab }
//                if (tabIndex in 0 until listTab.size) {
//                    viewpager.setCurrentItem(tabIndex, false)
//                }
//                    })
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        binding.imgTool.setOnSingleClickListener {
            onClickTool()
        }
    }

    init {
        binding.tabLock.setImageResource(R.drawable.ic_home_lock_selector)
        binding.tabLock.setTitleResource(R.string.tab_lock)

        binding.tabSecurity.setImageResource(R.drawable.ic_home_security_selector)
        binding.tabSecurity.setTitleResource(R.string.security)

        binding.tabThemes.setImageResource(R.drawable.ic_home_theme_selector)
        binding.tabThemes.setTitleResource(R.string.theme)

        binding.tabSettings.setImageResource(R.drawable.ic_home_setting_selector)
        binding.tabSettings.setTitleResource(R.string.settings)

        listTab.add(binding.tabLock)
        listTab.add(binding.tabSecurity)
        listTab.add(binding.tabThemes)
        listTab.add(binding.tabSettings)
    }
}