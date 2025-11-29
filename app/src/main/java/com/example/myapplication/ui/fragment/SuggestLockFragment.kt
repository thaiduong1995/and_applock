package com.example.myapplication.ui.fragment

import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.data.model.AppData
import com.example.myapplication.data.model.liveData.StateData
import com.example.myapplication.databinding.FragmentSuggestLokBinding
import com.example.myapplication.extention.FirebaseEvent
import com.example.myapplication.ui.adapter.SuggestAdapter
import com.example.myapplication.utils.Utils
import com.example.myapplication.view_model.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuggestLockFragment : BaseCacheFragment<FragmentSuggestLokBinding>() {

    private val mainViewModel by activityViewModels<MainViewModel>()
    private var adapter: SuggestAdapter? = null
    private var searchAdapter: SuggestAdapter? = null
    private var keyWordSearch: String = ""
    private var searchHandler = Handler(Looper.getMainLooper())
    private val listAppTotal = arrayListOf<AppData>()
    private val listAppSuggest = arrayListOf<AppData>()
    private val listAppSearch = arrayListOf<AppData>()
    private var searchRunnable = Runnable {
        mainViewModel.searchSuggestApp(keyWordSearch)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentSuggestLokBinding {
        return FragmentSuggestLokBinding.inflate(inflater, container, false)
    }

    override fun initListener() {
        binding.tvSkip.setOnClickListener(this)
        binding.swLockAll.setOnClickListener(this)
        binding.tvCreate.setOnClickListener(this)
        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            keyWordSearch = text.toString()
            searchHandler.removeCallbacks(searchRunnable)
            searchHandler.postDelayed(searchRunnable, 200)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent) {
        super.dispatchTouchEvent(event)
        val edittextRect = Rect()
        val recyclerRect = Rect()
        binding.edtSearch.getGlobalVisibleRect(edittextRect)
        binding.recycler.getGlobalVisibleRect(recyclerRect)
        if (!edittextRect.contains(event.rawX.toInt(), event.rawY.toInt()) && recyclerRect.contains(
                event.rawX.toInt(), event.rawY.toInt()
            )
        ) {
            binding.edtSearch.clearFocus()
            Utils.hideKeyboard(activity, binding.edtSearch)
        }
    }

    override fun initData() {
        listAppTotal.clear()
        listAppSuggest.clear()
        listAppSearch.clear()
        context?.let {
            mainViewModel.getSuggestApp(it)
        }
        mainViewModel.getListInstallApp()
    }

    override fun initObservers() {
        mainViewModel.listSuggestAppLiveData.observe(this) { stateData ->
            when (stateData.getStatus()) {
                StateData.DataStatus.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                StateData.DataStatus.SUCCESS -> {
                    stateData.getData()?.let {
                        listAppSuggest.addAll(it)
                        initAdapter(it)
                    }
                    binding.progressBar.visibility = View.GONE
                }

                StateData.DataStatus.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                }

                else -> {}
            }
        }

        mainViewModel.listSuggestSearchLiveData.observe(this) { stateData ->
            when (stateData.getStatus()) {
                StateData.DataStatus.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                StateData.DataStatus.SUCCESS -> {
                    listAppSearch.clear()
                    stateData.getData()?.let {
                        listAppSearch.addAll(it)
                        initSearchAdapter(it)
                    }
                    binding.progressBar.visibility = View.GONE
                }

                StateData.DataStatus.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                }

                else -> {}
            }
        }
    }

    private fun initAdapter(listApp: List<AppData>) {
        listAppTotal.clear()
        listAppTotal.addAll(listAppSearch)
        listAppTotal.addAll(listAppSuggest)
        adapter = SuggestAdapter(ArrayList(listApp))
        binding.recycler.layoutManager = GridLayoutManager(context, 4)
        binding.recycler.adapter = adapter
        adapter?.onItemSeclected = { isVisible ->
            binding.tvCreate.isVisible = isVisible
            activity?.let { act ->
                binding.tvCreate.text = String.format(
                    act.getString(
                        R.string.create_lock_icon,
                        adapter?.data?.filter { it.selected }?.size?.plus(
                            searchAdapter?.data?.filter { it.selected }?.size ?: 0
                        ),
                        listAppTotal.size
                    )
                )
            }
        }
    }

    private fun initSearchAdapter(listApp: List<AppData>) {
        listAppTotal.clear()
        listAppTotal.addAll(listAppSearch)
        listAppTotal.addAll(listAppSuggest)
        searchAdapter = SuggestAdapter(ArrayList(listApp))
        binding.recyclerSearch.layoutManager = GridLayoutManager(context, 4)
        binding.recyclerSearch.adapter = searchAdapter

        searchAdapter?.onItemSeclected = { isVisible ->
            binding.tvCreate.isVisible = isVisible
            activity?.let { act ->
                binding.tvCreate.text = String.format(
                    act.getString(
                        R.string.create_lock_icon,
                        adapter?.data?.filter { it.selected }?.size?.plus(
                            searchAdapter?.data?.filter { it.selected }?.size ?: 0
                        ),
                        listAppTotal.size
                    )
                )
            }
        }
    }

    override fun onViewClicked(view: View) {
        when (view.id) {
            R.id.tvSkip -> {
                setNavGraphId(R.navigation.home_graph)
            }

            R.id.tvCreate -> {
                val listAppSelected = mutableListOf<AppData>()
                adapter?.data?.filter { it.selected }?.let { listAppSelected.addAll(it) }
                searchAdapter?.data?.filter { it.selected }?.let { listAppSelected.addAll(it) }
                lockApps(listAppSelected)
                setNavGraphId(R.navigation.home_graph)
            }

            R.id.swLockAll -> {
                adapter?.data?.onEach { it.selected = binding.swLockAll.isChecked }
                searchAdapter?.data?.onEach { it.selected = binding.swLockAll.isChecked }
                adapter?.notifyDataSetChanged()
                searchAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun lockApps(listAppSelected: List<AppData>?) {
        context?.let {
            if (listAppSelected?.isNotEmpty() == true) {
                if (listAppSelected.size == adapter?.data?.size) {
                    FirebaseEvent.clickAll()
                } else {
                    FirebaseEvent.chooseApp(listAppSelected)
                }
                mainViewModel.lockApps(it, ArrayList(listAppSelected))
            }
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


    override fun onDetach() {
        super.onDetach()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }
}