package com.example.myapplication.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.data.model.BrowserType
import com.example.myapplication.data.model.HistoryBrowser
import com.example.myapplication.databinding.FragmentHistoryBinding
import com.example.myapplication.extention.gone
import com.example.myapplication.extention.hideKeyBoard
import com.example.myapplication.ui.adapter.HistoryBrowserAdapter
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.Utils
import com.example.myapplication.view_model.BrowserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class HistoryFragment : BaseCacheFragment<FragmentHistoryBinding>() {

    private var mAdapter: HistoryBrowserAdapter? = null
    private val mBrowserViewModel by viewModels<BrowserViewModel>()
    private val mListHistory: ArrayList<HistoryBrowser> = arrayListOf()
    private val mListBookMark: ArrayList<HistoryBrowser> = arrayListOf()
    private var browserType = BrowserType.HISTORY.value
    private var keyWordSearch: String = ""
    private val searchHandler = Handler(Looper.getMainLooper())

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentHistoryBinding {
        return FragmentHistoryBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        registerObserver()
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

    private fun initRecycler() {
        mAdapter = HistoryBrowserAdapter(onClickDelete, onClick)
        binding.rvHistory.layoutManager = LinearLayoutManager(context)
        binding.rvHistory.adapter = mAdapter
    }

    override fun getDataBundle() {
        browserType = arguments?.getInt(Constants.KEY_TYPE_BROWSER) ?: 0
        if (browserType == BrowserType.HISTORY.value) {
            binding.tvTitle.text = getString(R.string.history)
        } else {
            binding.tvTitle.text = getString(R.string.bookmark)
            binding.ctClearData.gone()
        }
    }

    private fun registerObserver() {
        mBrowserViewModel.listHistoryBrowser.observe(viewLifecycleOwner) {
            mListHistory.apply {
                clear()
                addAll(it)
            }
            if (browserType == BrowserType.HISTORY.value) {
                mAdapter?.setData(mListHistory)
            }
        }

        mBrowserViewModel.listBookMark.observe(viewLifecycleOwner) {
            mListBookMark.apply {
                clear()
                addAll(it)
            }
            if (browserType == BrowserType.BOOKMARK.value) {
                mAdapter?.setData(mListBookMark)
            }
        }

        mBrowserViewModel.listHistorySearch.observe(viewLifecycleOwner) {
            (it as? ArrayList<HistoryBrowser>)?.let { it1 -> mAdapter?.setData(it1) }
        }
    }

    private fun filterListBookMark(listBookMark: ArrayList<HistoryBrowser>): ArrayList<HistoryBrowser> {
        val list: ArrayList<HistoryBrowser> = arrayListOf()
        listBookMark.forEach {
            val itemBookMark = list.find { item ->
                it.url == item.url
            }
            if (itemBookMark == null) {
                list.add(it)
            }
        }
        return list
    }

    private val onClickDelete: (item: HistoryBrowser) -> Unit = {
        if (browserType == BrowserType.HISTORY.value) {
            mBrowserViewModel.deleteHistory(it)
        } else {
            mBrowserViewModel.deleteBookMark(it)
        }
    }
    private val onClick: (item: HistoryBrowser) -> Unit = {
        setFragmentResult(
            Constants.KEY_RESULT_BROWSER_FRAGMENT, bundleOf(Constants.KEY_RESULT_HISTORY to it)
        )
        popBackStack()
    }

    override fun initListener() {
        binding.imgBack.setOnClickListener(this)
        binding.tvClearData.setOnClickListener(this)
        binding.ivSearch.setOnClickListener(this)
        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            keyWordSearch = text.toString().lowercase(Locale.getDefault())
            searchHandler.removeCallbacks(searchRunnable)
            searchHandler.postDelayed(searchRunnable, 200)
        }
        binding.edtSearch.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                binding.edtSearch.clearFocus()
                binding.edtSearch.hideKeyBoard(this.requireContext())
                if (binding.edtSearch.text?.isEmpty() == true) {
                    binding.search.visibility = View.GONE
                    binding.tabBarHistory.visibility = View.VISIBLE
                    binding.ctClearData.visibility = View.VISIBLE
                }
            }
            false
        }
    }

    private var searchRunnable = Runnable {
        if (browserType == BrowserType.HISTORY.value) {
            mBrowserViewModel.searchHistory(keyWordSearch, mListHistory)
        } else {
            mBrowserViewModel.searchHistory(keyWordSearch, mListBookMark)
        }
    }

    override fun onViewClicked(view: View) {
        when (view.id) {
            R.id.imgBack -> popBackStack()
            R.id.tv_clear_data -> deleteAllData()
            R.id.iv_search -> searchHistory()
        }
    }

    private fun deleteAllData() {
        if (browserType == BrowserType.HISTORY.value) {
            mBrowserViewModel.deleteAllDataHistory()
        } else {
            mBrowserViewModel.deleteAllDataBookMark()
        }
    }

    private fun searchHistory() {
        binding.search.visibility = View.VISIBLE
        binding.tabBarHistory.visibility = View.GONE
        binding.ctClearData.visibility = View.GONE
        binding.edtSearch.requestFocus()
        Utils.showKeyboard(binding.edtSearch)
    }
}