package com.example.myapplication.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import com.cem.admodule.inter.PurchaseCallback
import com.cem.admodule.manager.PurchaseManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.data.model.liveData.StateData
import com.example.myapplication.databinding.FragmentPurchaseBinding
import com.example.myapplication.extention.FirebaseEvent
import com.example.myapplication.ui.fragment.purchase.PurchaseAdapter
import com.example.myapplication.ui.fragment.purchase.PurchaseModel
import com.example.myapplication.view_model.MainViewModel
import com.example.myapplication.utils.setSingleClick
import com.example.myapplication.utils.toast
import com.yarolegovich.discretescrollview.DSVOrientation
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class PurchaseFragment : BaseCacheFragment<FragmentPurchaseBinding>(), PurchaseCallback {

    private val mainViewModel: MainViewModel by viewModels()
    private var ID_PURCHASE = PurchaseManager.PURCHASE_WEEK
    private var adapter: PurchaseAdapter? = null

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentPurchaseBinding {
        return FragmentPurchaseBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSubscribe.setSingleClick {
            buyProduct(ID_PURCHASE)
        }
        binding.imvClose.setSingleClick {
            popBackStack()
        }

        binding.txtRestore.setSingleClick {
            restorePurchase()
        }
    }

    override fun initData() {
        super.initData()
        mainViewModel.getPurchase()
    }

    override fun initObservers() {
        super.initObservers()
        mainViewModel.purchaseLiveData.observe(viewLifecycleOwner) { stateData ->
            when (stateData.getStatus()) {
                StateData.DataStatus.LOADING -> {

                }

                StateData.DataStatus.SUCCESS -> {
                    stateData.getData()?.let {
                        initViewPagerAdapter(it)
                    }

                }

                StateData.DataStatus.ERROR -> {

                }

                else -> {}
            }
        }
    }

    private fun initViewPagerAdapter(data: List<PurchaseModel>) {
        val purchaseAdapter = PurchaseAdapter()
        purchaseAdapter.setData(data)
        val infiniteScrollAdapter: InfiniteScrollAdapter<PurchaseAdapter.ViewHolder> =
            InfiniteScrollAdapter.wrap(purchaseAdapter)
        binding.viewPager.apply {
            setOrientation(DSVOrientation.HORIZONTAL)
            addOnItemChangedListener { holder, adapterPosition ->
                val realPosition = infiniteScrollAdapter.getRealPosition(adapterPosition)
                data.getOrNull(realPosition)?.let {
                    ID_PURCHASE = it.idPurchase
                }
                binding.txtThreeDays.isInvisible = ID_PURCHASE != PurchaseManager.PURCHASE_WEEK
                Handler(Looper.getMainLooper()).post {
                    purchaseAdapter.setPosition(adapterPosition)
                }
            }
            adapter = infiniteScrollAdapter
            setSlideOnFling(true)
            setItemTransformer(
                ScaleTransformer.Builder().setMinScale(0.8f).build()
            )
            scrollToPosition(1)
        }
    }

    private fun buyProduct(id: String) {
        Timber.d(id)
        activity?.let {
            CoroutineScope(Dispatchers.Main).launch {
                if (PurchaseManager.instance?.isRemovedAds(it) == false) {
                    PurchaseManager.instance?.setCallback(this@PurchaseFragment)
                    PurchaseManager.instance?.purchaseSub(it, id)
                }
            }
        }
    }

    private fun restorePurchase() {
        activity?.let {
            CoroutineScope(Dispatchers.Main).launch {
                if (PurchaseManager.instance?.isRemovedAds(it) == false) {
                    PurchaseManager.instance?.setCallback(this@PurchaseFragment)
                    PurchaseManager.instance?.restorePurchases(it)
                }
            }
        }
    }

    override fun onPurchaseSuccess() {
        CoroutineScope(Dispatchers.Main).launch {
            FirebaseEvent.successPro(ID_PURCHASE)
            context?.toast(context?.getString(R.string.buy_premium_success).toString())
            //DataLocal.setIsVip(true)
        }
    }

    override fun onItemSuccess(productId: String, quantity: Int) {

    }

    override fun onPurchaseFailed() {
        context?.toast(context?.getString(R.string.buy_premium_fail).toString())
    }
}