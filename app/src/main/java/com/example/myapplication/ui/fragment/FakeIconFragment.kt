package com.example.myapplication.ui.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.admodule.manager.CemAdManager
import com.example.android.shortcutext.core.Shortcut
import com.example.android.shortcutext.core.ShortcutAction
import com.example.android.shortcutext.core.ShortcutExecutor
import com.example.android.shortcutext.setting.SettingPermission
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.data.model.AppData
import com.example.myapplication.data.model.liveData.StateData
import com.example.myapplication.databinding.FragmentFakeIconBinding
import com.example.myapplication.extention.FirebaseEvent
import com.example.myapplication.extention.toastMessageShortTime
import com.example.myapplication.ui.activities.LauncherActivity
import com.example.myapplication.ui.adapter.FakeIconAdapter
import com.example.myapplication.ui.dialog.DialogSelectIcon
import com.example.myapplication.utils.Utils
import com.example.myapplication.view_model.FakeIconViewModel

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FakeIconFragment : BaseCacheFragment<FragmentFakeIconBinding>() {

    private var adapter: FakeIconAdapter? = null
    private val viewModel by viewModels<FakeIconViewModel>()
    private var keyWordSearch: String = ""
    private val searchHandler = Handler(Looper.getMainLooper())

    private var searchRunnable = Runnable {
        FirebaseEvent.searchIcon(keyWordSearch)
        viewModel.searchApp(keyWordSearch)
    }

    private var currentPosition = -1
    private val createShortcutHandler = Handler(Looper.getMainLooper())
    private var createShortcutSuccess = false
    private val createShortcutRunnable = Runnable {
        if (createShortcutSuccess) {
            return@Runnable
        } else {
            createShortcutSuccess = false
            alertDialog.show()
        }
    }

    lateinit var alertDialog: AlertDialog

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentFakeIconBinding {
        return FragmentFakeIconBinding.inflate(inflater, container, false)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Shortcut.singleInstance.addShortcutCallback(callback)
        initDialog()
    }

    private fun initDialog() {
        context?.let { ct ->
            alertDialog = AlertDialog.Builder(ct).create()
            alertDialog.setTitle(ct.getString(R.string.need_request_access))
            alertDialog.setMessage(ct.getString(R.string.need_permission_shortcut))
            alertDialog.setButton(
                AlertDialog.BUTTON_POSITIVE, "OK"
            ) { _, _ ->
                gotoSetting()
            }
        }
    }

    private fun gotoSetting() {
        context?.let { ct ->
            SettingPermission(ct).start()
        }
    }

    override fun initData() {
        context?.let {
            viewModel.getAllApp(it)
        }
    }

    override fun initObservers() {
        viewModel.listAppLiveData.observe(this) {
            when (it.getStatus()) {
                StateData.DataStatus.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                StateData.DataStatus.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    it.getData()?.let {
                        adapter?.setData(ArrayList(it))
                    }
                }

                StateData.DataStatus.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                }

                else -> {}
            }
        }
    }

    override fun initUI() {
        initRecycler()
    }

    override fun initListener() {
        binding.imgBack.setOnClickListener(this)
        binding.imgHowToUse.setOnClickListener(this)
        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            keyWordSearch = text.toString()
            searchHandler.removeCallbacks(searchRunnable)
            searchHandler.postDelayed(searchRunnable, 200)
        }
    }

    private fun initRecycler() {
        adapter = FakeIconAdapter(listApp = ArrayList(), onAddIcon = { position, appName ->
            FirebaseEvent.chooseIcon(appName)
            currentPosition = position
            showDialogSelectIcon()
        }, onChangeIcon = { appData ->
            if (appData.fakeIcon == null) {
                context?.toastMessageShortTime(getString(R.string.you_not_select_icon))
            } else {
//                RewardedAdUtils.showRewardAd(context = activity,
//                    isShowReward = RemoteManager.rewardFakeIcon,
//                    onDismiss = {
//                        changeIcon(appData)
//                    }, onUserEarnedRewardListener = {
//                        Timber.d(it.type)
//                    })
            }
        })
        binding.recycler.layoutManager = LinearLayoutManager(context)
        binding.recycler.adapter = adapter
    }

    private fun changeIcon(appData: AppData) {
        FirebaseEvent.changeIcon()
        context?.let { ct ->
            val intentSortCut = Intent()
            intentSortCut.setAction(Intent.ACTION_VIEW)
                .putExtra("pkg", appData.packageName).component =
                ComponentName(ct.packageName, LauncherActivity::class.java.name)

            val shortcutInfoCompat: ShortcutInfoCompat =
                ShortcutInfoCompat.Builder(ct, System.currentTimeMillis().toString()).run {
                    setShortLabel(appData.appName)
                    setAlwaysBadged()
                    setIcon(
                        IconCompat.createWithBitmap(
                            appData.fakeIcon!!
                        )
                    )
                    setIntent(intentSortCut)
                    setActivity(
                        ComponentName(
                            ct.packageName, LauncherActivity::class.java.name
                        )
                    )
                    build()
                }

            // gọi xuống lib để tạo shortcut
            Shortcut.singleInstance.requestPinShortcut(
                context = ct, shortcutInfoCompat = shortcutInfoCompat, shortcutAction = action
            )
        }
    }

    private val action = object : ShortcutAction() {
        // nếu không check được quyền thì  sẽ gọi hàm này để mình bật dialog
        override fun showPermissionDialog(
            context: Context, check: Int, shortcutExecutor: ShortcutExecutor
        ) {
            showPermissionDialog()
        }

        // được goij khi goị hàm pỉnequestShortcut
        override fun onCreateAction(
            requestPinShortcut: Boolean, check: Int, shortcutExecutor: ShortcutExecutor
        ) {

        }

        // được gọi khi shortcut đã tồn tại và update shortcut
        override fun onUpdateAction(updatePinShortcut: Boolean) {
            createShortcutSuccess = true
        }
    }

    // hàm này được gọi khi tạo shortcut thành công => tắt đialog xin quyền đi
    private val callback: Shortcut.Callback by lazy {
        object : Shortcut.Callback {
            override fun onAsyncCreate(id: String, label: String) {
                createShortcutSuccess = true
                context?.toastMessageShortTime(getString(R.string.create_shortcut_success))
            }
        }
    }

    private fun showPermissionDialog() {
        createShortcutHandler.removeCallbacks(createShortcutRunnable)
        createShortcutHandler.postDelayed(
            createShortcutRunnable, DELAY_TIME_SHOW_DIALOG
        )
    }

    private fun showDialogSelectIcon() {
        val dialog = DialogSelectIcon.newInstance()
        dialog.onSaveIcon = {
            adapter?.updateData(currentPosition, it)
        }
        dialog.show(parentFragmentManager, "")
    }

    override fun onViewClicked(view: View) {
        when (view.id) {
            R.id.imgBack -> {
                popBackStack()
            }

            R.id.imgHowToUse -> {
                findNavController().navigate(R.id.howToUseScreen)
            }
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

    override fun onDetach() {
        super.onDetach()
        Shortcut.singleInstance.removeShortcutCallback(callback)
    }

    companion object {
        const val DELAY_TIME_SHOW_DIALOG = 1000L //miliseconds
    }
}