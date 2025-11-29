package com.example.myapplication.ui.fragment

import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.R
import com.example.myapplication.R.layout
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.data.model.Filter
import com.example.myapplication.data.model.liveData.StateData
import com.example.myapplication.databinding.FragmentLockBinding
import com.example.myapplication.extention.FirebaseEvent
import com.example.myapplication.extention.insertNativeToListAppData
import com.example.myapplication.ui.adapter.FilterAdapter
import com.example.myapplication.ui.adapter.InstalledAppAdapter
import com.example.myapplication.ui.custom.LinearItemDecoration
import com.example.myapplication.ui.custom.toPx
import com.example.myapplication.ui.dialog.DialogConfirmLockMoreFun
import com.example.myapplication.ui.dialog.DialogConfirmToUnlock
import com.example.myapplication.ui.dialog.DialogRequestAllPermission
import com.example.myapplication.utils.PermissionChecker
import com.example.myapplication.utils.Utils
import com.example.myapplication.view_model.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LockFragment : BaseCacheFragment<FragmentLockBinding>() {

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val adapter: InstalledAppAdapter = InstalledAppAdapter()
    private var searchHandler = Handler(Looper.getMainLooper())
    private var keyWordSearch: String = ""
    private var clickIconLockCount = 0

    private var searchRunnable = Runnable {
        FirebaseEvent.clickSearch(keyWordSearch)
        mainViewModel.searchApp(keyWordSearch)
    }

    override fun onStart() {
        super.onStart()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): FragmentLockBinding {
        return FragmentLockBinding.inflate(inflater, container, false)
    }

    override fun initUI() {
        initRecycler()
    }

    private fun initRecycler() {
        binding.recycler.adapter = adapter
        if (0 == binding.recycler.itemDecorationCount) {
            binding.recycler.addItemDecoration(
                LinearItemDecoration(
                    0f.toPx.toInt(), 4f.toPx.toInt()
                )
            )
        }
        binding.recycler.layoutManager = LinearLayoutManager(context)
        adapter.onLockStateChanged = { position, appData ->
//            val isShowInterstitial = clickIconLockCount >= 2 && RemoteManager.interLock
//            if (clickIconLockCount >= 5 && !DataLocal.isVip() && !appData.isLock) {
//                showDialogConfirmPremium()
//            } else {
//                AdsFullManager.showInterstitial(
//                    context = activity,
//                    isShowRemote = isShowInterstitial,
//                    onDismiss = {
            if (appData.isLock) {
                appData.isLock = false
                clickIconLockCount -= 1
                adapter.updateItem(position, appData)
            } else {
                clickIconLockCount += 1
                FirebaseEvent.lockApp(appData.appName)
                appData.isLock = true
                adapter.updateItem(position, appData)
            }

            context?.let {
                mainViewModel.lockOrUnLockApp(it, appData)
            }
//                    })
//            }
        }
    }

    private fun showDialogConfirmToUnlockApp(isCheck: Boolean) {
        val dialog = DialogConfirmToUnlock.newInstance(isCheck)
        if (!dialog.isShown) {
            dialog.show(childFragmentManager, "")
        }
        dialog.callback = {
            mainViewModel.changeSateLockAllApp(binding.root.context, binding.swLockAll.isChecked)
        }
        dialog.onCancel = {
            binding.swLockAll.isChecked = !binding.swLockAll.isChecked
        }
    }

    private fun showDialogConfirmPremium() {
        val dialog = DialogConfirmLockMoreFun.newInstance()
        if (!dialog.isShown) {
            dialog.show(childFragmentManager, "")
        }
        dialog.callbackConfirm = {
            findNavController().navigate(R.id.purchaseScreen)
            binding.swLockAll.isChecked = false
        }
        dialog.callbackCancel = {
            binding.swLockAll.isChecked = false
        }
    }

    override fun initListener() {
        binding.swLockAll.setOnClickListener(this)
        binding.imgSearch.setOnClickListener(this)
        binding.tvCancel.setOnClickListener(this)
        binding.imgFilter.setOnClickListener(this)
        binding.imgVip.setOnClickListener(this)
        binding.rgCategory.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbAll -> {
                    FirebaseEvent.viewHome("All")
                    mainViewModel.getListInstallApp()
                }

                R.id.rbNewApp -> {
                    FirebaseEvent.viewHome("News App")
                    mainViewModel.getNewInstallApp()
                }

                R.id.rbSystemApp -> {
                    FirebaseEvent.viewHome("System apps")
                    mainViewModel.getListSystemApp()
                }
            }
        }
        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            keyWordSearch = text.toString()
            searchHandler.removeCallbacks(searchRunnable)
            searchHandler.postDelayed(searchRunnable, 200)
        }
        editorActionListener(binding.edtSearch)

    }

    private fun editorActionListener(editText: EditText) {
        editText.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                FirebaseEvent.clickSearch(keyWordSearch)
                mainViewModel.searchApp(keyWordSearch)
                Utils.hideKeyboard(activity, view)
                editText.clearFocus()
            }
            false
        }
    }

    override fun initObservers() {
        mainViewModel.listLockedAppLiveData.observe(this) { stateData ->
            when (stateData.getStatus()) {
                StateData.DataStatus.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                StateData.DataStatus.SUCCESS -> {
                    stateData.getData()?.let {
                        if (binding.layoutSearch.isGone) {
                            adapter.setData(ArrayList(it.insertNativeToListAppData()))
                        } else {
                            adapter.setData(ArrayList(it))
                        }
                        binding.swLockAll.isChecked =
                            it.isNotEmpty() && it.filter { it.isLock }.size == it.size
                        clickIconLockCount = it.filter { it.isLock }.size
                    }
                    binding.progressBar.visibility = View.GONE
                }

                StateData.DataStatus.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                }

                else -> {}
            }
        }

//        mainViewModel.listLockConditionLiveData.observe(this) { stateData ->
//            when (stateData.getStatus()) {
//                StateData.DataStatus.SUCCESS -> {
//                    Log.d("thinhvh54", "listLockConditionLiveData: ")
//                    stateData.getData()?.let {
//                        adapter.setLockConditions(ArrayList(it))
//                    }
//                }
//                else -> {}
//            }
//        }
        mainViewModel.getListInstallApp()
//        mainViewModel.getListConditionLock()
    }

    override fun onViewClicked(view: View) {
        when (view.id) {
            R.id.swLockAll -> {
                if (CemAdManager.getInstance(view.context).isVip()) {
                    showDialogConfirmToUnlockApp(binding.swLockAll.isChecked)
                } else {
                    showDialogConfirmPremium()
                }
            }

            R.id.imgSearch -> {
                binding.layoutSearch.visibility = View.VISIBLE
                binding.layoutCategory.visibility = View.GONE
                binding.edtSearch.requestFocus()
                Utils.showKeyboard(binding.edtSearch)
            }

            R.id.tvCancel -> {
                binding.layoutSearch.visibility = View.GONE
                binding.layoutCategory.visibility = View.VISIBLE
                binding.edtSearch.setText("")
                binding.edtSearch.clearFocus()
                Utils.hideKeyboard(activity, view)
            }

            R.id.imgFilter -> {
                showDialogFilterApp()
            }

            R.id.imgVip -> {
                FirebaseEvent.clickPro()
                findNavController().navigate(R.id.purchaseScreen)
            }
        }
    }

    override fun checkPermissions() {
        context?.let {
            if (!PermissionChecker.isAllPermissionChecked(it)) {
                showDialogRequestAllPermission()
            }
        }
    }

    private fun showDialogFilterApp() {
        val popupWindow = PopupWindow(
            LayoutInflater.from(context).inflate(layout.layout_popup_filter, null),
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        val recycleView = popupWindow.contentView.findViewById<RecyclerView>(R.id.recycler)
        recycleView.layoutManager = LinearLayoutManager(context)
        val filterList = ArrayList(Filter.values().toList()).onEach { it.isSelected = false }
        filterList.find { it.name == mainViewModel.filterType.name }?.isSelected = true
        recycleView.adapter = FilterAdapter(filterList) {
            popupWindow.dismiss()
            mainViewModel.changeFilter(it)
        }
        popupWindow.showAsDropDown(
            binding.imgFilter
        )
    }

    private fun showDialogRequestAllPermission() {
        val dialog = DialogRequestAllPermission.newInstance()
        showBottomSheetDialogFragment(dialog)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onResume() {
        super.onResume()
        binding.tvLockAll.isSelected = CemAdManager.getInstance(binding.tvLockAll.context).isVip()
        binding.imgVip.isGone = CemAdManager.getInstance(binding.tvLockAll.context).isVip()
    }

    companion object {

        @JvmStatic
        fun newInstance() = LockFragment().apply {
            arguments = bundleOf()
        }
    }
}