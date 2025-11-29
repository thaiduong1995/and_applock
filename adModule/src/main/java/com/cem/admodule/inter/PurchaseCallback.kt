package com.cem.admodule.inter

interface PurchaseCallback {
    fun onPurchaseSuccess()

    fun onItemSuccess(productId: String, quantity: Int)

    fun onPurchaseFailed()
}