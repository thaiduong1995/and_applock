package com.example.myapplication.ui.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.data.model.Intruder
import com.example.myapplication.databinding.FragmentIntruderRecordBinding
import com.example.myapplication.extention.insertNativeToListIntruder
import com.example.myapplication.ui.adapter.IntruderAdapter
import com.example.myapplication.ui.dialog.DialogConfirmToDelete
import com.example.myapplication.utils.setSingleClick
import com.example.myapplication.view_model.CaptureIntrudersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntruderRecordsFragment : BaseCacheFragment<FragmentIntruderRecordBinding>() {

    private val viewModel by activityViewModels<CaptureIntrudersViewModel>()
    private var adapter: IntruderAdapter? = null

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentIntruderRecordBinding {
        return FragmentIntruderRecordBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        initAdapter()
        viewModel.getIntruder()
    }

    override fun initObservers() {
        viewModel.listImageLiveData?.observe(this) {
            it?.let { data ->
                adapter?.submitList(data.insertNativeToListIntruder())
            }
            if (it.isEmpty()) {
                adapter?.isShowDeleteLocation = false
                setDeleteLocationIcon()
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun initAdapter() {
//        adapter = IntruderAdapter( onClickEvent = {
//            RewardedAdUtils.showRewardAd(context = activity,
//                isShowReward = RemoteManager.rewardIntruderRecord,
//                onDismiss = {
//                    FirebaseEvent.clickDetailReview()
//                    findNavController().navigate(
//                        R.id.intruderDetailsScreen, bundleOf(
//                            Constants.KEY_INTRUDER to it
//                        )
//                    )
//                })
//        }, onDeleteClickListener = { intruder: Intruder, i: Int ->
//            showDialogDelete(intruder, i)
//        })
        binding.recycler.layoutManager = LinearLayoutManager(context)
        binding.recycler.adapter = adapter
    }

    private fun showDialogDelete(intruder: Intruder, position: Int) {
        val dialog = DialogConfirmToDelete.newInstance(getString(R.string.confirm_delete_time))
        if (!dialog.isShown) {
            dialog.show(childFragmentManager, "")
        }
        dialog.callback = {
            viewModel.deleteIntruder(intruder)
            adapter?.removeLocation(intruder, position)
        }
    }

    override fun initListener() {
        binding.imgBack.setOnClickListener {
            popBackStack()
        }

        binding.ivRemove.setSingleClick {
            adapter?.changeDeleteLocationState()
            setDeleteLocationIcon()
        }
    }

    private fun setDeleteLocationIcon() {
        binding.ivRemove.setImageResource(if (adapter?.isShowDeleteLocation == true) R.drawable.ic_cancel else R.drawable.ic_trash_wifi)
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