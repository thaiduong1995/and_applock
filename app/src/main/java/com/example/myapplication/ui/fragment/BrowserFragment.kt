package com.example.myapplication.ui.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.data.model.*
import com.example.myapplication.databinding.FragmentBrowserBinding
import com.example.myapplication.extention.FirebaseEvent
import com.example.myapplication.extention.parcelable
import com.example.myapplication.ui.adapter.BrowserAdapter
import com.example.myapplication.ui.custom.LinearItemDecoration
import com.example.myapplication.ui.custom.toPx
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.PreferenceHelper
import com.example.myapplication.utils.clearTime
import com.example.myapplication.view_model.BrowserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass.
 * Use the [BrowserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class BrowserFragment : BaseCacheFragment<FragmentBrowserBinding>() {

    @Inject
    lateinit var preferences: PreferenceHelper

    private val mBrowserViewModel by activityViewModels<BrowserViewModel>()
    private var mAdapter: BrowserAdapter? = null
    private var mCurrentTab = TabBrowser()
    private var mCurrentHistory = HistoryBrowser()
    private val mBrowserList: ArrayList<BrowserItem> = arrayListOf()
    private var mTabList: List<TabBrowser> = arrayListOf()
    private var mListHistory: List<HistoryBrowser> = arrayListOf()
    private var listBookMark: List<HistoryBrowser> = arrayListOf()
    private var mListHistoryCurrentTab: List<HistoryBrowser> = arrayListOf()

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentBrowserBinding {
        return FragmentBrowserBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initWebView()
        initRecycler()
        registerObserver()
    }

    private fun addDataToList() {
        mBrowserList.addAll(
            listOf(
                BrowserItem(
                    title = R.string.social_media,
                    mList = mutableListOf(
                        MediaItem(
                            R.string.google,
                            R.drawable.ic_chrome,
                            "https://www.google.com.vn/"
                        ),
                        MediaItem(
                            R.string.facebook,
                            R.drawable.ic_facebook,
                            "https://www.facebook.com/"
                        ),
                        MediaItem(
                            R.string.instagram,
                            R.drawable.ic_instagram,
                            "https://www.instagram.com/"
                        ),
                        MediaItem(R.string.skype, R.drawable.ic_skype, "https://www.skype.com/")
                    )
                ),
                BrowserItem(
                    title = R.string.entertainment,
                    mList = mutableListOf(
                        MediaItem(
                            R.string.bbc_news, R.drawable.ic_cnn, "https://edition.cnn.com/"
                        ),
                        MediaItem(
                            R.string.twitter, R.drawable.ic_twiiter, "https://twitter.com/"
                        ),
                        MediaItem(
                            R.string.youtube, R.drawable.ic_youtube, "https://www.youtube.com/"
                        )
                    )
                ),
                BrowserItem(
                    title = R.string.shopping,
                    mList = mutableListOf(
                        MediaItem(
                            R.string.amazon, R.drawable.ic_amazon, "https://www.amazon.com/"
                        ),
                        MediaItem(
                            R.string.google_shopping,
                            R.drawable.ic_shopping,
                            "https://shopping.google.com/"
                        )
                    )
                )
            )
        )
    }

    private fun registerObserver() {
        mBrowserViewModel.listTabsBrowser.observe(viewLifecycleOwner) {
            binding.layoutBottomNavBrowser.textTab.text =
                if (it.isEmpty()) "1" else it.size.toString()
            mTabList = it
            val index = preferences.getIndexMultipleTabs()
            if (mTabList.isNotEmpty() && mTabList.size > index) {
                mCurrentTab = mTabList[index]
                if (mCurrentTab.url.isEmpty()) {
                    visibleWebView(false)
                } else {
                    visibleWebView(true)
                    searchOrLoad(mCurrentTab.url)
                }
            } else {
                visibleWebView(false)
            }
        }

        mBrowserViewModel.listHistoryBrowser.observe(viewLifecycleOwner) {
            mListHistory = it
            mBrowserViewModel.getListHistoryCurrentTab(mCurrentTab.id)
            if (it.isNotEmpty()) {
                mCurrentHistory = it.last()
            }
        }
        mBrowserViewModel.listBookMark.observe(viewLifecycleOwner) {
            listBookMark = it
        }

        mBrowserViewModel.listHistoryCurrentTab.observe(viewLifecycleOwner) {
            mListHistoryCurrentTab = it
            if (mListHistoryCurrentTab.isEmpty()) {
                visibleWebView(false)
            } else if (mCurrentTab.url.isNotEmpty()) {
                visibleWebView(true)
                if (mListHistoryCurrentTab.last().id != mCurrentHistory.id) {
                    searchOrLoad(mListHistoryCurrentTab.last().url)
                }
            }
        }

        setFragmentResultListener(Constants.KEY_RESULT_BROWSER_FRAGMENT) { key, bundle ->
            if (key == Constants.KEY_RESULT_BROWSER_FRAGMENT) {
                val resultData = bundle.parcelable<HistoryBrowser>(Constants.KEY_RESULT_HISTORY)
                resultData?.url?.let { searchOrLoad(it) }
            }

            if (key == Constants.KEY_RESULT_MULTIPLE_FRAGMENT) {
                visibleWebView(false)
            }
        }

        enableViewBackOrNext()
    }

    private fun initRecycler() {
        mAdapter = BrowserAdapter(mBrowserList, onMediaClick)
        binding.rvBrowser.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
            if (0 == binding.rvBrowser.itemDecorationCount) {
                addItemDecoration(
                    LinearItemDecoration(0f.toPx.roundToInt(), 4f.toPx.roundToInt())
                )
            }
        }
    }

    override fun initListener() {
        binding.imgBack.setOnClickListener(this)
        binding.layoutBottomNavBrowser.ivHome.setOnClickListener(this)
        binding.layoutBottomNavBrowser.ivNext.setOnClickListener(this)
        binding.layoutBottomNavBrowser.ivPrevious.setOnClickListener(this)
        binding.layoutBottomNavBrowser.ivMultipleTab.setOnClickListener(this)
        binding.ivRotate.setOnClickListener(this)
        binding.ivBook.setOnClickListener(this)
        binding.ivBookmark2.setOnClickListener(this)
        binding.layoutBottomNavBrowser.ivBookmark.setOnClickListener(this)
        editorActionListener(binding.edtSearch)
    }

    private fun editorActionListener(editText: EditText) {
        editText.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                searchOrLoad(editText.text.toString())
                hideKeyboard()
                editText.clearFocus()
            }
            false
        }
    }

    override fun onViewClicked(view: View) {
        when (view.id) {
            R.id.imgBack -> {
                onClickMultipleTabs()
                popBackStack()
            }

            R.id.iv_home -> {
                visibleWebView(false)
            }

            R.id.iv_next -> {
                if (binding.webView.canGoForward() && binding.webView.isVisible) binding.webView.goForward()
            }

            R.id.iv_previous -> {
                if (binding.webView.canGoBack() && binding.webView.isVisible) binding.webView.goBack()
            }

            R.id.iv_rotate -> {
                binding.webView.reload()
            }

            R.id.iv_multiple_tab -> {
                onClickMultipleTabs()
                findNavController().navigate(R.id.multipleTabBrowserScreen)
            }

            R.id.iv_book -> {
                findNavController().navigate(
                    R.id.historyBrowserScreen, bundleOf(
                        Constants.KEY_TYPE_BROWSER to BrowserType.HISTORY.value
                    )
                )
            }

            R.id.iv_bookmark2 -> {
                updateBookMark()
            }

            R.id.iv_bookmark -> {
                findNavController().navigate(
                    R.id.historyBrowserScreen, bundleOf(
                        Constants.KEY_TYPE_BROWSER to BrowserType.BOOKMARK.value
                    )
                )
            }
        }
    }

    private fun visibleWebView(isVisible: Boolean) {
        if (isVisible) {
            binding.apply {
                webView.visibility = View.VISIBLE
                ivRotate.visibility = View.VISIBLE
                ivBookmark2.visibility = View.VISIBLE
                rvBrowser.visibility = View.GONE
                imgSearch2.visibility = View.GONE
                tabBarBrowser.visibility = View.GONE
            }
        } else {
            binding.apply {
                edtSearch.text?.clear()
                webView.visibility = View.GONE
                ivRotate.visibility = View.GONE
                ivBookmark2.visibility = View.GONE
                rvBrowser.visibility = View.VISIBLE
                imgSearch2.visibility = View.VISIBLE
                tabBarBrowser.visibility = View.VISIBLE
            }
        }
        enableViewBackOrNext()
    }

    private val onMediaClick: (String) -> Unit = {
        searchOrLoad(it)
    }

    private fun searchOrLoad(text: String) {
        visibleWebView(true)
        if (Patterns.WEB_URL.matcher(text.lowercase()).matches()) {
            if (text.contains("http://")) {
                binding.webView.loadUrl(text.replace("http://", "https://"))
            } else if (text.contains("https://")) {
                binding.webView.loadUrl(text)
            } else {
                binding.webView.loadUrl("https://$text")
            }
        } else {
            binding.webView.loadUrl("https://www.google.com/search?q=$text")
        }
        FirebaseEvent.clickUrl()
        hideKeyboard()
    }

    private fun checkBookMark() {
        val hasBookMark = listBookMark.find { mCurrentHistory.url == it.url }
        if (hasBookMark == null) {
            binding.ivBookmark2.setImageResource(R.drawable.ic_bookmarks)
        } else {
            binding.ivBookmark2.setImageResource(
                R.drawable.ic_book_mark_true
            )
            mCurrentHistory.isBookMark = true
        }
    }

    private fun saveHistory() {
        CoroutineScope(Dispatchers.Main).launch {
            val calendar = Calendar.getInstance()
            val currentCalendar = calendar.clearTime().timeInMillis
            val hasTime = mListHistory.find {
                it.time == currentCalendar
            }
            if (hasTime == null) {
                val time = HistoryBrowser()
                time.time = currentCalendar
                time.isBookMark = true
                mBrowserViewModel.insertHistory(time)
            }
            binding.webView.apply {
                mCurrentHistory = HistoryBrowser()
                mCurrentHistory.idTabBrowser = mCurrentTab.id
                mCurrentHistory.title = title.toString()
                mCurrentHistory.image =
                    favicon?.let { mBrowserViewModel.bitMapToString(it) }.toString()
                mCurrentHistory.url = url.toString()
                mCurrentHistory.time = currentCalendar
                mBrowserViewModel.insertHistory(mCurrentHistory)
            }
            checkBookMark()
        }
    }

    private fun updateBookMark() {
        val calendar = Calendar.getInstance()
        val currentCalendar = calendar.clearTime().timeInMillis
        mCurrentHistory.time = currentCalendar
        mCurrentHistory.isBookMark = !mCurrentHistory.isBookMark
        if (mCurrentHistory.isBookMark) {
            FirebaseEvent.clickBookmark()
            binding.ivBookmark2.setImageResource(R.drawable.ic_book_mark_true)
        } else {
            binding.ivBookmark2.setImageResource(R.drawable.ic_bookmarks)
        }
        mBrowserViewModel.updateBookMark(mCurrentHistory)
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onPause() {
        super.onPause()
        binding.webView.onPause()
        //conflict phần quảng cáo
//        binding.webView.pauseTimers()
    }

    override fun onResume() {
        super.onResume()
        binding.webView.onResume()
//        binding.webView.resumeTimers()
    }


    private fun initWebView() {
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                if ("https://www.google.com/" != url) {
                    binding.edtSearch.setText(url)
                } else {
                    binding.edtSearch.text?.clear()
                }
                enableViewBackOrNext()
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                saveHistory()
                enableViewBackOrNext()
            }
        }

        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedIcon(view: WebView, icon: Bitmap) {
                super.onReceivedIcon(view, icon)
                mBrowserViewModel.updateImageHistory(
                    mBrowserViewModel.bitMapToString(icon), mCurrentHistory.id
                )
            }
        }
    }

    fun enableViewBackOrNext() {
        binding.layoutBottomNavBrowser.ivNext.apply {
            if (binding.webView.canGoForward() && binding.webView.isVisible) {
                alpha = 1f
                isEnabled = true
            } else {
                alpha = 0.5f
                isEnabled = false
            }
        }
        binding.layoutBottomNavBrowser.ivPrevious.apply {
            if (binding.webView.canGoBack() && binding.webView.isVisible) {
                alpha = 1f
                isEnabled = true
            } else {
                alpha = 0.5f
                isEnabled = false
            }
        }
    }

    private fun getBitmapFromViewUsingCanvas(view: View): String {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return mBrowserViewModel.bitMapToString(bitmap)
    }

    private fun onClickMultipleTabs() {
        val image = if (binding.webView.isVisible) {
            getBitmapFromViewUsingCanvas(binding.webView)
        } else {
            getBitmapFromViewUsingCanvas(binding.rvBrowser)
        }
        if (mTabList.isEmpty()) {
            preferences.setImageDefault(image)
            preferences.setIndexMultipleTabs(0)
            mCurrentTab = TabBrowser(image, binding.edtSearch.text.toString())
            mBrowserViewModel.insertTab(mCurrentTab)
        } else {
            mCurrentTab.image = image
            mCurrentTab.url = binding.edtSearch.text.toString()
            mBrowserViewModel.updateTab(mCurrentTab)
        }
    }

    override fun onStart() {
        super.onStart()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
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

    override fun onDestroy() {
        super.onDestroy()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    init {
        addDataToList()
    }
}