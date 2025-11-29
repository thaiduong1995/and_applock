package com.suntech.mytools.purchase

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.suntech.mytools.mytools.datalocal.DataLocal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class PurchaseManager private constructor() {
    private var billingClient: BillingClient? = null
    private var callback: PurchaseCallback? = null
    private var skuDetailsListIAP: List<ProductDetails>? = ArrayList()
    private var skuDetailsListSUB: List<ProductDetails>? = ArrayList()
    private var skuDetailsListNIAP: List<ProductDetails>? = ArrayList()
    private var isPurchased = false
    private val acknowledgePurchaseResponseListener = AcknowledgePurchaseResponseListener {
        if (callback != null) {
            callback!!.purchaseSuccess()
        }
    }

    fun setPurchased(purchased: Boolean) {
        isPurchased = purchased
    }

    fun setCallback(callback: PurchaseCallback?) {
        this.callback = callback
    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases -> // To be implemented in a later section.
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (i in purchases.indices) {
                    handlePurchase(purchases[i])
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                try {
                    if (callback != null) {
                        callback!!.purchaseFail()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                try {
                    if (callback != null) {
                        callback!!.purchaseFail()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    private suspend fun initBilling(context: Context?) {
        billingClient = BillingClient.newBuilder(context!!).setListener(purchasesUpdatedListener)
            .enablePendingPurchases().build()
        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    val queryInAppPurchase = QueryProductDetailsParams.newBuilder().setProductList(
                        listOf(
                            QueryProductDetailsParams.Product.newBuilder().setProductId(ID_PURCHASE)
                                .setProductType(BillingClient.ProductType.INAPP).build()
                        )
                    ).build()
                    billingClient?.queryProductDetailsAsync(
                        queryInAppPurchase
                    ) { _, productIdList ->
                        Log.d(TAG, "onBillingInPurchase: $productIdList")
                        this@PurchaseManager.skuDetailsListIAP = productIdList
                    }

                    val queryCoinsPurchase = QueryProductDetailsParams.newBuilder().setProductList(
                        listOf(
                            QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(PRODUCT_ID_5_COINS).setProductType(
                                    BillingClient.ProductType.INAPP
                                ).build(),
                            QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(PRODUCT_ID_10_COINS).setProductType(
                                    BillingClient.ProductType.INAPP
                                ).build(),
                            QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(PRODUCT_ID_15_COINS).setProductType(
                                    BillingClient.ProductType.INAPP
                                ).build(),
                            QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(PRODUCT_ID_20_COINS).setProductType(
                                    BillingClient.ProductType.INAPP
                                ).build(),
                        )
                    ).build()
                    billingClient?.queryProductDetailsAsync(
                        queryCoinsPurchase
                    ) { _, productIdList ->
                        Log.d(TAG, "onBillingCoins: $productIdList")
                        this@PurchaseManager.skuDetailsListNIAP = productIdList
                    }

                    val querySubPurchase = QueryProductDetailsParams.newBuilder().setProductList(
                        listOf(
                            QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(PRODUCT_SUB_MONTH).setProductType(
                                    BillingClient.ProductType.SUBS
                                ).build(),
                            QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(PRODUCT_SUB_YEAR).setProductType(
                                    BillingClient.ProductType.SUBS
                                ).build(),
                            QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(PRODUCT_SUB_WEEK).setProductType(
                                    BillingClient.ProductType.SUBS
                                ).build()
                        )
                    ).build()

                    billingClient?.queryProductDetailsAsync(
                        querySubPurchase
                    ) { _, productIdList ->
                        Log.d(TAG, "onBillingSub: $productIdList")
                        this@PurchaseManager.skuDetailsListSUB = productIdList
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        val isVip = withContext(Dispatchers.IO) {
                            isRemovedAds(context)
                        }
                        DataLocal.setIsVip(isVip)
                        Log.e(TAG, "isRemovedAds= ${DataLocal.isVip()}")
                    }
                }
            }

            override fun onBillingServiceDisconnected() {

            }
        })
    }

    private fun handlePurchase(purchase: Purchase) {
        //chỗ này mình xử lý cho thằng in app purchase
        //đối với gói subs và in app premium sử dụng noconsume
        //gói mua tiem sử dụng consume
        if (skuDetailsListNIAP?.any {
                purchase.products.contains(it.productId)
            } == true) {
            consume(purchase)
        } else {
            noConsume(purchase)
        }
    }

    private fun consume(purchase: Purchase) {
        val consumeParams =
            ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        billingClient!!.consumeAsync(consumeParams) { billingResult: BillingResult, s: String? ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (productId in skuDetailsListNIAP!!) {
                    for (sku in purchase.products) {
                        if (sku.contains(productId.productId)) {
                            Log.d(TAG, "consume: $sku")
//                                    callback?.purchaseCoin(sku, purchase.quantity)
                        }
                    }
                }
            } else {
                try {
                    if (callback != null) {
                        callback!!.purchaseFail()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun noConsume(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams =
                    AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken)
                        .build()
                billingClient!!.acknowledgePurchase(
                    acknowledgePurchaseParams, acknowledgePurchaseResponseListener
                )
            }
        }
    }


    suspend fun isRemovedAds(context: Context?): Boolean {
        if (billingClient == null) {
            if (context == null) {
                return false
            }
            initBilling(context)
        }
        return isPurchased() || isSubscribed()
    }

    fun hasSubVip(): Boolean {
        return isPurchased
    }

    private suspend fun isPurchased(): Boolean {
        val queryPurchaseParams =
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP)
                .build()
        var purchaseResult = billingClient?.queryPurchasesAsync(queryPurchaseParams)
        if (purchaseResult?.purchasesList?.isEmpty() == true) return false
        purchaseResult?.purchasesList?.listIterator()?.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (purchase.products.contains(ID_PURCHASE)) return true
            }
        }
        return false
    }

    private suspend fun isSubscribed(): Boolean {
        val queryPurchaseParams =
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        val purchaseResult = billingClient?.queryPurchasesAsync(queryPurchaseParams)
        if (purchaseResult?.purchasesList?.isEmpty() == true) return false
        purchaseResult?.purchasesList?.listIterator()?.forEach { purchase ->
            for (productId in purchase.products) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    return true
                }
            }
        }
        return false
    }

    suspend fun purchase(activity: Activity?, productId: String) {
        if (activity == null) {
            return
        }
        if (billingClient == null) {
            initBilling(activity)
            return
        }
        val productDetails = getSkuDetail(skuDetailsListIAP!!, productId)
        Log.d(TAG, "purchase: $productDetails")
        productDetails?.let {
            val billingFlowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(it)
                        .build()
                )
            ).build()
            billingClient?.launchBillingFlow(activity, billingFlowParams)
        }
    }

    suspend fun purchaseCoin(activity: Activity?, productId: String) {
        if (activity == null) {
            return
        }
        if (billingClient == null) {
            initBilling(activity)
            return
        }
        val productDetails = getSkuDetail(skuDetailsListNIAP!!, productId)
        Log.d(TAG, "purchase: $productDetails")
        productDetails?.let {
            val billingFlowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(it)
                        .build()
                )
            ).build()
            billingClient?.launchBillingFlow(activity, billingFlowParams)
        }
    }

    suspend fun subscribe(activity: Activity?, productId: String) {
        if (activity == null) {
            return
        }
        if (billingClient == null) {
            initBilling(activity)
            return
        }
        val productDetails = getSkuDetail(skuDetailsListSUB!!, productId)
        productDetails?.let {
            val offerToken = it.subscriptionOfferDetails?.get(0)?.offerToken ?: return
            val billingFlowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(it)
                        .setOfferToken(offerToken).build()
                )
            ).build()
            billingClient?.launchBillingFlow(activity, billingFlowParams)
        }
    }

    suspend fun restorePurchases(context: Context?) {
        if (billingClient == null) {
            initBilling(context)
        }

        val queryPurchaseParams =
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        val purchaseResult = billingClient?.queryPurchasesAsync(queryPurchaseParams)
        if (purchaseResult?.purchasesList?.isEmpty() == true) {
            callback?.purchaseFail()
        } else {
            purchaseResult?.purchasesList?.listIterator()?.forEach { purchase ->
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    DataLocal.setIsVip(true)
                    callback?.purchaseSuccess()
                    return@forEach
                } else {
                    callback?.purchaseFail()
                }
            }
        }
    }

    private fun getSkuDetail(
        skuDetailsList: List<ProductDetails>?, productId: String
    ): ProductDetails? {
        if (skuDetailsList != null) {
            for (productDetail in skuDetailsList) {
                Log.d(TAG, "getProductDetail: $skuDetailsList")
                if (productDetail.productId == productId) {
                    return productDetail
                }
            }
        }
        return null
    }

    fun getPrice(productId: String): String? {
        if (billingClient == null || !billingClient!!.isReady) {
            return null
        }
        if (skuDetailsListSUB != null) {
            for (skuDetails in skuDetailsListSUB!!) {
                if (skuDetails.productId == productId) {
                    return skuDetails.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(
                        0
                    )?.formattedPrice
                }
            }
        }
        if (skuDetailsListIAP != null) {
            for (skuDetails in skuDetailsListIAP!!) {
                if (skuDetails.productId == productId) {
                    return skuDetails.oneTimePurchaseOfferDetails?.formattedPrice
                }
            }
        }
        if (skuDetailsListNIAP != null) {
            for (skuDetails in skuDetailsListNIAP!!) {
                if (skuDetails.productId == productId) {
                    return skuDetails.oneTimePurchaseOfferDetails?.formattedPrice
                }
            }
        }
        return null
    }

    companion object {
        private val TAG = PurchaseManager::class.java.name
        const val PRODUCT_SUB_YEAR = "com.cem.applock.hideapps.password.yearly"
        const val PRODUCT_SUB_MONTH = "com.cem.applock.hideapps.password.month"
        const val PRODUCT_SUB_WEEK = "com.cem.applock.hideapps.password.weekly"
        const val ID_PURCHASE = "android.test.purchased"
        const val PRODUCT_ID_5_COINS = "android.test.purchased"
        const val PRODUCT_ID_10_COINS = "android.test.purchased"
        const val PRODUCT_ID_15_COINS = "android.test.purchased"
        const val PRODUCT_ID_20_COINS = "android.test.purchased"

        @get:Synchronized
        @SuppressLint("StaticFieldLeak")
        var instance: PurchaseManager? = null
            get() {
                if (field == null) {
                    field = PurchaseManager()
                }
                return field
            }
            private set
    }
}