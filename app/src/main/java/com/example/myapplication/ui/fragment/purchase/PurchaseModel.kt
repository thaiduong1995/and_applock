package com.example.myapplication.ui.fragment.purchase

import com.cem.admodule.manager.PurchaseManager
import com.example.myapplication.R

sealed class PurchaseModel(
    var idPurchase: String = "",
    var idName: Int = 0,
    var price: String = "",
    var priceMonth: String = "",
    val description: Int? = null
) {
    class PurchaseMonthModel(
        idPurchase: String = PurchaseManager.PURCHASE_MONTH,
        idName: Int = R.string.month,
        price: String = "$9.99",
        priceMonth: String = "$9.99/month",
        description: Int? = null
    ) : PurchaseModel(idPurchase, idName, price, priceMonth, description)

    class PurchaseWeekModel(
        idPurchase: String = PurchaseManager.PURCHASE_WEEK,
        idName: Int = R.string.week,
        price: String = "$4.99",
        priceMonth: String = "$19.99/month",
        description: Int? = R.string.with_days_free
    ) : PurchaseModel(idPurchase, idName, price, priceMonth, description)

    class PurchaseYearModel(
        idPurchase: String = PurchaseManager.PURCHASE_YEAR,
        idName: Int = R.string.year,
        price: String = "$29.99",
        priceMonth: String = "$2.49/month",
        description: Int? = null
    ) : PurchaseModel(idPurchase, idName, price, priceMonth, description)
}