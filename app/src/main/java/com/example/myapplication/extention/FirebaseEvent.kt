package com.example.myapplication.extention

import android.os.Bundle
import androidx.core.os.bundleOf
import com.example.myapplication.data.model.AppData
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object FirebaseEvent {


    fun logViewCreateScreen() {
        setParamsEvent(VIEW_CREATE)
    }

    fun createLock(lockOptionName: String) {
        val bundle = bundleOf(
            LOCK_OPTION_NAME to lockOptionName
        )
        setParamsEvent(DONE_CREATE, bundle)
    }

    fun chooseApp(listApp: List<AppData>) {
        val bundle = Bundle().apply {
            listApp.forEach {
                putString(APP_NAME, it.appName)
            }
        }
        setParamsEvent(CHOOSE_APP, bundle)
    }

    fun clickAll() {
        setParamsEvent(CLICK_ALL)
    }

    fun fillUsage() {
        setParamsEvent(FILL_USAGE)
    }

    fun fillShowUp() {
        setParamsEvent(FILL_SHOW_UP)
    }

    fun viewHome(tabName: String) {
        val bundle = bundleOf(
            TAB_NAME to tabName
        )
        setParamsEvent(VIEW_HOME, bundle)
    }

    fun clickPro() {
        setParamsEvent(CLICK_PRO)
    }

    fun successPro(packageName: String) {
        val bundle = bundleOf(
            PACKAGE_NAME to packageName
        )
        setParamsEvent(SUCCESS_PRO, bundle)
    }

    fun clickSearch(appName: String) {
        val bundle = bundleOf(
            APP_NAME to appName
        )
        setParamsEvent(CLICK_SEARCH, bundle)
    }

    fun lockApp(appName: String) {
        val bundle = bundleOf(
            APP_NAME to appName
        )
        setParamsEvent(LOCK_APP, bundle)
    }

    fun viewSecurity() {
        setParamsEvent(VIEW_SECURITY)
    }

    fun chooseLock(nameLock: String) {
        val bundle = bundleOf(
            NAME_LOCK to nameLock
        )
        setParamsEvent(CHOOSE_LOCK, bundle)
    }

    fun clickSettingsLock(nameLock: String) {
        val bundle = bundleOf(
            NAME_LOCK to nameLock
        )
        setParamsEvent(CLICK_SETTINGS_LOCK, bundle)
    }

    fun viewTools() {
        setParamsEvent(VIEW_TOOLS)
    }

    fun viewBrowser() {
        setParamsEvent(VIEW_BROWSER)
    }

    fun clickUrl() {
        setParamsEvent(CLICK_URL)
    }

    fun clickBookmark() {
        setParamsEvent(CLICK_BOOKMARK)
    }

    fun viewTimeLock() {
        setParamsEvent(VIEW_TIME_LOCK)
    }

    fun addTime() {
        setParamsEvent(ADD_TIME)
    }

    fun saveTime() {
        setParamsEvent(SAVE_TIME)
    }

    fun viewLocationLock() {
        setParamsEvent(VIEW_LOCATION_LOCK)
    }

    fun addLocation() {
        setParamsEvent(ADD_LOCATION)
    }

    fun saveLocation() {
        setParamsEvent(SAVE_LOCATION)
    }

    fun viewWifiLock() {
        setParamsEvent(VIEW_WIFI_LOCK)
    }

    fun saveWifi() {
        setParamsEvent(SAVE_WIFI)
    }

    fun viewCaptureIntruders() {
        setParamsEvent(VIEW_CAPTURE_INTRUDERS)
    }

    fun switchCapture(isEnable: Boolean) {
        val bundle = bundleOf(
            STATUS_SWITCH to isEnable
        )
        setParamsEvent(SWITCH_CAPTURE, bundle)
    }

    fun chooseTime(time: String) {
        val bundle = bundleOf(
            OPTION_NAME to time
        )
        setParamsEvent(CHOOSE_TIME, bundle)
    }

    fun clickReview() {
        setParamsEvent(CLICK_REVIEW)
    }

    fun clickDetailReview() {
        setParamsEvent(CLICK_DETAIL_REVIEW)
    }

    fun viewFakeCover(nameKey: String) {
        setParamsEvent(nameKey)
    }

    fun switchCover(enable: Boolean) {
        setParamsEvent(SWITCH_COVER, bundleOf(STATUS_SWITCH to enable))
    }

    fun chooseCover(nameKey: String) {
        val bundle = bundleOf(
            SIGNALS_NAME to nameKey
        )
        setParamsEvent(CHOOSE_COVER, bundle)
    }

    fun viewFakeIcons() {
        setParamsEvent(VIEW_FAKE_ICONS)
    }

    fun searchIcon(appName: String) {
        val bundle = bundleOf(
            APP_NAME to appName
        )
        setParamsEvent(SEARCH_ICON, bundle)
    }

    fun chooseIcon(iconName: String) {
        val bundle = bundleOf(
            ICON_NAME to iconName
        )
        setParamsEvent(CHOOSE_ICON, bundle)
    }

    fun saveIcon(type: Boolean) {
        val bundle = bundleOf(
            TYPE to if (type) "Icon" else "Ảnh"
        )
        setParamsEvent(SAVE_ICON, bundle)
    }

    fun changeIcon() {
        setParamsEvent(CHANGE_ICON)
    }

    fun viewCleaner() {
        setParamsEvent(VIEW_CLEANER)
    }

    fun clickScan() {
        setParamsEvent(CLICK_SCAN)
    }

    fun chooseOptionScan(scanName: String) {
        val bundle = bundleOf(
            SCAN_NAME to scanName
        )
        setParamsEvent(CHOOSE_OPTION_SCAN, bundle)
    }

    fun clickClean() {
        setParamsEvent(CLICK_CLEAN)
    }

    fun viewTheme() {
        setParamsEvent(VIEW_THEME)
    }

    fun viewDetailTheme() {
        setParamsEvent(VIEW_DETAIL_THEME)
    }

    fun clickCustom(typeCustom: String) {
        setParamsEvent(typeCustom)
    }

    fun clickCreate(itemName: String) {
        setParamsEvent(CLICK_CREATE, bundleOf(ITEM_NAME to itemName))
    }

    fun viewSettings() {
        setParamsEvent(VIEW_SETTINGS)
    }

    fun clickScreenSettings(keyValue: String) {
        setParamsEvent(keyValue)
    }

    fun confirmQuestion(questionId: String) {
        val bundle = bundleOf(
            QUESTION_ID to questionId
        )
        setParamsEvent(CONFIRM_QUESTION, bundle)
    }

    private fun setParamsEvent(params: String, bundle: Bundle? = null) {
        Firebase.analytics.logEvent(params, bundle)
    }

    const val VIEW_FAKE_COVER = "view_fake_cover"
    const val CLICK_CUSTOM_THEME = "click_custom_theme"
    const val CLICK_CUSTOM_PATTERN = "click_custom_pattern"
    const val CLICK_CUSTOM_PIN = "click_custom_pin"
    const val CLICK_CUSTOM_KNOCK = "click_custom_knock"
    private const val VIEW_CREATE = "view_create"
    private const val DONE_CREATE = "done_create"
    private const val LOCK_OPTION_NAME = "lock_option_name"
    private const val CHOOSE_APP = "choose_app"
    private const val APP_NAME = "app_name"
    private const val CLICK_ALL = "click_all"
    private const val FILL_USAGE = "fill_usage"
    private const val FILL_SHOW_UP = "fill_show_up"
    private const val VIEW_HOME = "view_home"
    private const val TAB_NAME = "tab_name"
    private const val CLICK_PRO = "click_pro"
    private const val SUCCESS_PRO = "success_pro"
    private const val PACKAGE_NAME = "package_name"
    private const val CLICK_SEARCH = "click_search"
    private const val LOCK_APP = "lock_app"
    private const val VIEW_SECURITY = "view_security"
    private const val CHOOSE_LOCK = "choose_lock"
    private const val NAME_LOCK = "name_lock"
    private const val CLICK_SETTINGS_LOCK = "click_settings_lock"
    private const val VIEW_TOOLS = "view_tools"
    private const val VIEW_BROWSER = "view_browser"
    private const val CLICK_URL = "click_url"
    private const val CLICK_BOOKMARK = "click_bookmark"
    private const val VIEW_TIME_LOCK = "view_time_lock"
    private const val ADD_TIME = "add_time"
    private const val SAVE_TIME = "save_time"
    private const val VIEW_LOCATION_LOCK = "view_location_lock"
    private const val ADD_LOCATION = "add_location"
    private const val SAVE_LOCATION = "save_location"
    private const val VIEW_WIFI_LOCK = "view_wifi_lock"
    private const val SAVE_WIFI = "save_wifi"
    private const val VIEW_CAPTURE_INTRUDERS = "view_capture_intruders"
    private const val SWITCH_CAPTURE = "switch_capture"
    private const val STATUS_SWITCH = "status_switch"
    private const val CHOOSE_TIME = "choose_time"
    private const val OPTION_NAME = "option_name"
    private const val CLICK_REVIEW = "click_review"
    private const val CLICK_DETAIL_REVIEW = "click_detail_review"
    private const val SWITCH_COVER = "switch_cover"
    private const val CHOOSE_COVER = "choose_cover"
    private const val SIGNALS_NAME = "signals_name"
    private const val VIEW_FAKE_ICONS = "view_fake_icons"
    private const val SEARCH_ICON = "search_icon"
    private const val CHOOSE_ICON = "choose_icon"
    private const val ICON_NAME = "icon_name"
    private const val SAVE_ICON = "save_icon"
    private const val TYPE = "type"
    private const val CHANGE_ICON = "save_icon"
    private const val VIEW_CLEANER = "view_cleaner"
    private const val CLICK_SCAN = "click_scan"
    private const val CHOOSE_OPTION_SCAN = "choose_option_scan"
    private const val SCAN_NAME = "scan_name"
    private const val CLICK_CLEAN = "click_clean"
    private const val VIEW_THEME = "view_theme"
    private const val VIEW_DETAIL_THEME = "view_detail_theme"
    private const val CLICK_CREATE = "click_create"
    private const val ITEM_NAME = "item_name"
    private const val VIEW_SETTINGS = "view_settings"
    const val CLICK_BANNER_PRO = "click_banner_pro"
    const val SWITCH_LOCK = "switch_lock"
    const val SWITCH_VIBRATION = "switch_vibration"
    const val SWITCH_UNINSTALL_PROTECTION = "switch_uninstall_protection"
    const val SWITCH_LOCK_NEW = "switch_lock_new"
    const val SWITCH_QUESTION = "switch_question"
    const val CLICK_SHARE = "click_share"
    const val CLICK_RATE = "click_rate"
    const val CLICK_FAQ = "click_faq"
    private const val CONFIRM_QUESTION = "confirm_question"
    private const val QUESTION_ID = "question_id"
}