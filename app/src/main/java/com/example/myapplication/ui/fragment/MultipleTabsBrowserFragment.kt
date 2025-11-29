package com.example.myapplication.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.data.model.TabBrowser
import com.example.myapplication.databinding.FragmentMultipleTabsBrowserBinding
import com.example.myapplication.ui.adapter.MultipleTabsBrowserAdapter
import com.example.myapplication.ui.dialog.DialogConfirmToDelete
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.PreferenceHelper
import com.example.myapplication.view_model.BrowserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [MultipleTabsBrowserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class MultipleTabsBrowserFragment : BaseCacheFragment<FragmentMultipleTabsBrowserBinding>() {

    private val mBrowserViewModel by viewModels<BrowserViewModel>()
    private var adapter: MultipleTabsBrowserAdapter? = null
    private val mTabList: List<TabBrowser> = arrayListOf()
    private var isDelete: Boolean = false
    private var isAdd: Boolean = false

    @Inject
    lateinit var preferences: PreferenceHelper

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentMultipleTabsBrowserBinding {
        return FragmentMultipleTabsBrowserBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        registerObserver()
    }

    private fun registerObserver() {
        mBrowserViewModel.listTabsBrowser.observe(viewLifecycleOwner) {
            (mTabList as? ArrayList<TabBrowser>)?.apply {
                clear()
                addAll(it)
            }
            if (it.isEmpty() && isDelete) {
                mBrowserViewModel.getDataListTabBrowser()
                setFragmentResult(
                    Constants.KEY_RESULT_MULTIPLE_FRAGMENT, bundleOf()
                )
                popBackStack()
            } else {
                (it as? ArrayList<TabBrowser>)?.let { list ->
                    adapter?.setData(list)
                }
                if (isAdd) {
                    onClickTab.invoke(mTabList.lastIndex)
                }
            }
        }
    }

    private fun initRecycler() {
        adapter = MultipleTabsBrowserAdapter(arrayListOf(), onClickTab, onClickRemove)
        binding.rvMultipleTabs.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = adapter
        }
    }

    private val onClickTab: (index: Int) -> Unit = {
        preferences.setIndexMultipleTabs(it)
        mBrowserViewModel.getDataListTabBrowser()
        popBackStack()
    }

    private val onClickRemove: (id: Long) -> Unit = {
        val currentIndex = preferences.getIndexMultipleTabs()
        val idTabCurrent = mTabList.getOrNull(currentIndex)?.id
        if (idTabCurrent == it) {
            val index = if (currentIndex > 0) currentIndex - 1 else 0
            preferences.setIndexMultipleTabs(index)
        }
        isDelete = true
        mBrowserViewModel.deleteTab(it)
    }

    override fun initListener() {
        binding.ivBack.setOnClickListener(this)
        binding.ivAdd.setOnClickListener(this)
        binding.ivDelete.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.iv_back -> popBackStack()
            R.id.iv_add -> addTab()
            R.id.iv_delete -> removeAll()
        }
    }

    private fun addTab() {
        val newTab = TabBrowser(preferences.getImageDefault(), "")
        mBrowserViewModel.insertTab(newTab)
        isAdd = true
    }

    private fun removeAll() {
        val dialog = DialogConfirmToDelete.newInstance(getString(R.string.confirm_delete_time))
        if (!dialog.isShown) {
            dialog.show(childFragmentManager, "")
        }
        dialog.callback = {
            mBrowserViewModel.deleteAllData()
            setFragmentResult(
                Constants.KEY_RESULT_MULTIPLE_FRAGMENT, bundleOf()
            )
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
}