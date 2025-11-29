package com.cem.admodule.ext

import android.view.View
import com.cem.admodule.data.AdConfig
import com.cem.admodule.data.AdManager
import com.cem.admodule.data.AdUnitItem
import org.json.JSONObject

fun fromAdManagerToJson(jsonObject: JSONObject): AdManager? {
    return try {
        val adManager = AdManager()
        adManager.adConfig = AdConfig.fromJson(jsonObject.optJSONObject(ConstAd.configs))
        adManager.adUnitList = getAdUnitItemCollection(jsonObject.optJSONObject(ConstAd.units))
        adManager
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun toJson(adManager: AdManager?): JSONObject? {
    if (adManager == null) return null
    return try {
        val jsonObject = JSONObject()
        if (adManager.adConfig != null) {
            jsonObject.put(ConstAd.configs, AdConfig.toJson(adManager.adConfig))
        }
        if (adManager.adUnitList != null) {
            jsonObject.put(ConstAd.units, getAdUnitItemToJson(adManager.adUnitList))
        }
        jsonObject
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun getAdUnitItemToJson(collectionData: Map<String, List<AdUnitItem>?>?): JSONObject? {
    if (collectionData == null) return null
    return try {
        val jsonObject = JSONObject()
        for ((key, adUnit) in collectionData) {
            if (adUnit != null) {
                val jsonArray = AdUnitItem.jsonFromArray(adUnit)
                if (jsonArray != null) jsonObject.put(key, jsonArray)
            }
        }
        jsonObject
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun getAdUnitItemCollection(jsonObject: JSONObject?): Map<String, List<AdUnitItem>>? {
    if (jsonObject == null) return null
    val resultValue: MutableMap<String, List<AdUnitItem>> = HashMap()
    val keys = jsonObject.keys()
    while (keys.hasNext()) {
        val keyValue = keys.next()
        val itemCollection = jsonObject.optJSONArray(keyValue)
        resultValue[keyValue] = AdUnitItem.arrayFromJson(itemCollection)
    }
    return resultValue
}


fun getAdCollection(
    adManager: AdManager, adKey: String?
): List<AdUnitItem>? {
    if (adKey.isNullOrEmpty()) return null
    val adUnitData = adManager.adUnitList

    return if (adUnitData == null || !adUnitData.containsKey(adKey)) {
        null
    } else adUnitData[adKey]
}


fun getAdUnit(unitsId: MutableList<AdUnitItem>): AdUnitItem? {
    if (unitsId.isEmpty()) return null
    for (unit in unitsId) {
        if (unit.enable) return unit
    }
    return null
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}
