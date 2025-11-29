package com.cem.admodule.manager

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchaseHistoryParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchaseHistory
import com.android.billingclient.api.queryPurchasesAsync
import com.cem.admodule.inter.PurchaseCallback
import com.cem.firebase_module.qonversion.QOnVersionConfig
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class PurchaseManager private constructor() {

    private var callback: PurchaseCallback? = null
    private var billingClient: BillingClient? = null
    private var skuDetailsListIAP: List<ProductDetails>? = ArrayList()
    private var skuDetailsListSUB: List<ProductDetails>? = ArrayList()
    private var skuDetailsListNIAP: List<ProductDetails>? = ArrayList()

    fun setCallback(callback: PurchaseCallback) {
        this.callback = callback
    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (!purchases.isNullOrEmpty()) {
                    QOnVersionConfig.onVersionPurchase()
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                } else {
                    try {
                        callback?.onPurchaseFailed()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                try {
                    callback?.onPurchaseFailed()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                try {
                    callback?.onPurchaseFailed()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    private suspend fun initBilling(context: Context?) {
        if (context == null) {
            Log.d(TAG, "initBilling: context null")
            return
        }
        billingClient = BillingClient.newBuilder(context).setListener(purchasesUpdatedListener)
            .enablePendingPurchases().build()
        billingClient?.startConnectSuspend()?.let { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "initBilling: vào response")
                processPurchases()
                val isVip = withContext(Dispatchers.IO) {
                    isRemovedAds(context)
                }
                Log.d(TAG, "isRemovedAds: $isVip")
                CemAdManager.getInstance(context).initMMKV().setIsVip(isVip)
            }
        }
    }

    private suspend fun BillingClient.startConnect() = callbackFlow {
        this@startConnect.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                channel.trySend(null)
                channel.close()
                Log.d(TAG, "onBillingServiceDisconnected: ")
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                Log.d(TAG, "onBillingSetupFinished: ")
                channel.trySend(billingResult)
                channel.close()
            }
        })
        awaitClose {
            channel.close()
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun BillingClient.startConnectSuspend() : BillingResult? = suspendCancellableCoroutine { const ->
        this@startConnectSuspend.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Log.d(TAG, "onBillingServiceDisconnected: ")
                if (const.isActive) const.resume(null)
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                Log.d(TAG, "onBillingSetupFinished: ")
                if (const.isActive) const.resume(billingResult)
            }
        })
    }

    private suspend fun processPurchases() {
        val queryInAppPurchase = QueryProductDetailsParams.newBuilder()
        queryInAppPurchase.setProductList(
            listOf(getProductINAPP(ID_PURCHASE))
        )
        val productIAPResult = withContext(Dispatchers.IO) {
            billingClient?.queryProductDetails(params = queryInAppPurchase.build())
        }

        productIAPResult?.productDetailsList?.let { data ->
            this.skuDetailsListIAP = data
            Log.d(TAG, "productIAPResult: ${Gson().toJson(data)}")
        }

        val queryItemsPurchase = QueryProductDetailsParams.newBuilder()
        queryItemsPurchase.setProductList(
            listOf(
                getProductINAPP(ONE_CREDITS), getProductINAPP(TWO_CREDITS),
                getProductINAPP(THREE_CREDITS), getProductINAPP(
                    FOUR_CREDITS
                )
            )
        )
        val productNIAPResult = withContext(Dispatchers.IO) {
            billingClient?.queryProductDetails(params = queryItemsPurchase.build())
        }
        productNIAPResult?.productDetailsList?.let { data ->
            this.skuDetailsListNIAP = data
            Log.d(TAG, "productNIAPResult: ${Gson().toJson(data)}")
        }

        val querySUBPurchase = QueryProductDetailsParams.newBuilder()
        querySUBPurchase.setProductList(
            listOf(
                getProductSUB(PURCHASE_WEEK),
                getProductSUB(PURCHASE_MONTH),
                getProductSUB(PURCHASE_YEAR)
            )
        )
        val productSUBResult = withContext(Dispatchers.IO) {
            billingClient?.queryProductDetails(params = querySUBPurchase.build())
        }
        productSUBResult?.productDetailsList?.let { data ->
            this.skuDetailsListSUB = data
            Log.d(TAG, "productSUBResult: ${Gson().toJson(data)}")
        }
    }

    private fun getProductINAPP(product: String): QueryProductDetailsParams.Product {
        return QueryProductDetailsParams.Product.newBuilder().setProductId(product)
            .setProductType(BillingClient.ProductType.INAPP).build()
    }

    private fun getProductSUB(product: String): QueryProductDetailsParams.Product {
        return QueryProductDetailsParams.Product.newBuilder().setProductId(product)
            .setProductType(BillingClient.ProductType.SUBS).build()
    }

    private fun handlePurchase(purchase: Purchase) {
        CoroutineScope(Dispatchers.IO).launch {
            if (purchase.products.contains(ID_PURCHASE)
                || purchase.products.contains(PURCHASE_YEAR)
                || purchase.products.contains(PURCHASE_MONTH)
                || purchase.products.contains(PURCHASE_WEEK)
            ) {
                noConsume(purchase)
            } else {
                consumeItem(purchase)
            }
        }
    }

    private suspend fun consumeItem(purchase: Purchase) {
        val consumeParams =
            ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken)
        val consumeResult = withContext(Dispatchers.IO) {
            billingClient?.consumePurchase(consumeParams.build())
        }
        consumeResult?.billingResult?.let { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (productId in skuDetailsListNIAP!!) {
                    for (sku in purchase.products) {
                        if (sku.contains(productId.productId)) {
                            callback?.onItemSuccess(productId.productId, purchase.quantity)
                            return
                        }
                    }
                }
            } else {
                try {
                    if (callback != null) {
                        callback!!.onPurchaseFailed()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private suspend fun noConsume(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams =
                    AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken)
                        .build()
                val ackPurchaseCallback = withContext(Dispatchers.IO) {
                    billingClient?.acknowledgePurchase(acknowledgePurchaseParams)
                }
                ackPurchaseCallback?.let { result ->
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        callback?.onPurchaseSuccess()
                    }
                }
            }
        }
    }

    suspend fun isRemovedAds(context: Context?): Boolean {
        if (billingClient == null) {
            initBilling(context)
        }
        return isPurchased() || isSubscribed()
    }

    private suspend fun isPurchased(): Boolean {
        val queryPurchaseParams =
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP)
                .build()
        val purchaseResult = billingClient?.queryPurchasesAsync(queryPurchaseParams)
        if (purchaseResult?.purchasesList.isNullOrEmpty()) return false
        purchaseResult?.purchasesList?.listIterator()?.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (purchase.products.contains(ID_PURCHASE)) return true
            }
        }
        return false
    }

    private suspend fun isSubscribed(): Boolean {
        val queryPurchaseParams =
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS)
                .build()
        val purchaseResult = billingClient?.queryPurchasesAsync(queryPurchaseParams)
        if (purchaseResult?.purchasesList.isNullOrEmpty()) return false
        purchaseResult?.purchasesList?.listIterator()?.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                return true
            }
        }
        return false
    }

    suspend fun purchasePremium(context: Activity?, product: String) {
        if (context == null) {
            return
        }

        if (billingClient == null) {
            initBilling(context)
            return
        }
        val productDetails = getProductDetails(skuDetailsListIAP, product)
        Log.d(TAG, "purchasePremium: $productDetails")
        productDetails?.let {
            val billingFlowParams =
                BillingFlowParams.newBuilder().setProductDetailsParamsList(
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails).build()
                    )
                ).build()
            billingClient?.launchBillingFlow(context, billingFlowParams)
        }
    }

    suspend fun purchaseItem(context: Activity?, product: String) {
        if (context == null) {
            return
        }

        if (billingClient == null) {
            initBilling(context)
            return
        }
        val productDetails = getProductDetails(skuDetailsListNIAP, product)
        Log.d(TAG, "purchaseItem: $productDetails")
        productDetails?.let {
            val billingFlowParams =
                BillingFlowParams.newBuilder().setProductDetailsParamsList(
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails).build()
                    )
                ).build()
            billingClient?.launchBillingFlow(context, billingFlowParams)
        }
    }

    suspend fun purchaseSub(context: Activity?, product: String) {
        if (context == null) {
            return
        }
        if (billingClient == null) {
            initBilling(context)
            return
        }
        val productDetails = getProductDetails(skuDetailsListSUB, product)
        Log.d(TAG, "purchaseItem: $productDetails")
        productDetails?.let {
            val offerToken = it.subscriptionOfferDetails?.get(0)?.offerToken ?: return
            val billingFlowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(it)
                        .setOfferToken(offerToken).build()
                )
            ).build()
            billingClient?.launchBillingFlow(context, billingFlowParams)
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
            callback?.onPurchaseFailed()
        } else {
            purchaseResult?.purchasesList?.listIterator()?.forEach { purchase ->
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    callback?.onPurchaseSuccess()
                    return@forEach
                } else {
                    callback?.onPurchaseFailed()
                }
            }
        }
    }

    //check item đó đã mua
    suspend fun checkPurchaseItemFirst(context: Context?, productId: String): Boolean {
        if (billingClient == null) {
            initBilling(context)
        }
        val params =
            QueryPurchaseHistoryParams.newBuilder().setProductType(BillingClient.ProductType.INAPP)

        val purchaseResult = billingClient?.queryPurchaseHistory(params.build())
        if (purchaseResult?.purchaseHistoryRecordList?.isEmpty() == true) return false
        purchaseResult?.purchaseHistoryRecordList?.listIterator()?.forEach { purchase ->
            if (purchase.products.contains(productId)) return true
        }
        return false
    }

    //check sub đó đã mua
    suspend fun checkPurchaseSubFirst(context: Context?, productId: String): Boolean {
        if (billingClient == null) {
            initBilling(context)
        }
        val params =
            QueryPurchaseHistoryParams.newBuilder().setProductType(BillingClient.ProductType.SUBS)

        val purchaseResult = billingClient?.queryPurchaseHistory(params.build())
        if (purchaseResult?.purchaseHistoryRecordList?.isEmpty() == true) return false
        purchaseResult?.purchaseHistoryRecordList?.listIterator()?.forEach { purchase ->
            if (purchase.products.contains(productId)) return true
        }
        return false
    }

    private fun getProductDetails(
        skuDetailsList: List<ProductDetails>?,
        product: String
    ): ProductDetails? {
        if (skuDetailsList == null) return null
        for (productDetail in skuDetailsList) {
            Log.d(TAG, "getProductDetail: $skuDetailsList")
            if (productDetail.productId == product) {
                return productDetail
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
        val TAG: String = PurchaseManager::class.java.simpleName

        const val ID_PURCHASE = "com.aesthetic.iconpack.iconchanger.premium"

        const val PURCHASE_YEAR = "com.aesthetic.iconpack.yearly"
        const val PURCHASE_MONTH = "com.aesthetic.iconpack.monthly"
        const val PURCHASE_WEEK = "com.aesthetic.iconpack.weekly"

        const val ONE_CREDITS = "com.aesthetic.iconpack.iconchanger.5coins"

        const val TWO_CREDITS = "com.aesthetic.iconpack.iconchanger.10coins"

        const val THREE_CREDITS = "com.aesthetic.iconpack.iconchanger.15coins"

        const val FOUR_CREDITS = "com.aesthetic.iconpack.iconchanger.20coins"

        @get:Synchronized
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
