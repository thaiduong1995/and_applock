package com.example.myapplication.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.myapplication.R
import com.example.myapplication.data.model.AppTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Thinhvh on 23/09/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
@Singleton
class PreferenceHelper @Inject constructor(@ApplicationContext private val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        context.packageName, Context.MODE_PRIVATE
    )

    fun setUnlockFirst(type: Boolean) {
        sharedPreferences.edit().putBoolean(ERROR_FIRST, type).apply()
    }

    fun getLockFirst(): Boolean {
        return sharedPreferences.getBoolean(ERROR_FIRST, false)
    }

    fun setTimeUnLock(type: Long) {
        sharedPreferences.edit().putLong(TIME_UN_LOCK, type).apply()
    }

    fun getTimeUnLock(): Long {
        return sharedPreferences.getLong(TIME_UN_LOCK, 0L)
    }

    fun getPinCode(): String {
        return sharedPreferences.getString(KEY_PIN, "") ?: ""
    }

    fun setPinCode(pinCode: String) {
        sharedPreferences.edit().putString(KEY_PIN, pinCode).apply()
    }

    fun getPatternCode(): String {
        return sharedPreferences.getString(KEY_PATTERN, "") ?: ""
    }

    fun setPatternCode(patternCode: String) {
        sharedPreferences.edit().putString(KEY_PATTERN, patternCode).apply()
    }

    fun getKnockCode(): String {
        return sharedPreferences.getString(KEY_KNOCK_CODE, "") ?: ""
    }

    fun setKnockCode(knockCode: String) {
        sharedPreferences.edit().putString(KEY_KNOCK_CODE, knockCode).apply()
    }

    fun getLockFrequencyType(): Int {
        return sharedPreferences.getInt(KEY_LOCK_FREQUENCY_TYPE, 0)
    }

    fun setLockFrequencyType(type: Int) {
        sharedPreferences.edit().putInt(KEY_LOCK_FREQUENCY_TYPE, type).apply()
    }

    fun getAnimation(): Int {
        return sharedPreferences.getInt(KEY_UNLOCK_ANIMATION, 0)
    }

    fun setAnimation(type: Int) {
        sharedPreferences.edit().putInt(KEY_UNLOCK_ANIMATION, type).apply()
    }

    fun getRandomKeyboard(): Boolean {
        return sharedPreferences.getBoolean(KEY_RANDOM_KEYBOARD, false)
    }

    fun setRandomKeyboard(enable: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_RANDOM_KEYBOARD, enable).apply()
    }

    fun isHidePatternTrails(): Boolean {
        return sharedPreferences.getBoolean(KEY_HIDE_PATTERN_TRAILS, false)
    }

    fun setHidePatternTrails(enable: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_HIDE_PATTERN_TRAILS, enable).apply()
    }

    fun getLockType(): Int {
        return sharedPreferences.getInt(KEY_LOCK_TYPE, -1)
    }

    fun setLockType(type: Int) {
        sharedPreferences.edit().putInt(KEY_LOCK_TYPE, type).apply()
    }

    fun isEnableFingerPrint(): Boolean {
        return sharedPreferences.getBoolean(KEY_FINGERPRINT_ENABLE, false)
    }

    fun setEnableFingerPrint(enable: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_FINGERPRINT_ENABLE, enable).apply()
    }

    fun getRecommendSignals(): Int {
        val defValue = R.string.your_app_is_stopped
        return sharedPreferences.getInt(KEY_RECOMMEND_SIGNALS, defValue)
    }

    fun setRecommendSignals(value: Int) {
        sharedPreferences.edit().putInt(KEY_RECOMMEND_SIGNALS, value).apply()
    }

    fun isFakeCoverEnable(): Boolean {
        return sharedPreferences.getBoolean(KEY_FAKE_COVER, false)
    }

    fun setEnableFakeCover(enable: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_FAKE_COVER, enable).apply()
    }

    fun getUnlockCount(): Int {
        return sharedPreferences.getInt(KEY_UNLOCK_COUNT, 3)
    }

    fun setUnlockCount(type: Int) {
        sharedPreferences.edit().putInt(KEY_UNLOCK_COUNT, type).apply()
    }

    fun isCaptureIntrudersEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_CAPTURE_INTRUDERS, false)
    }

    fun setEnableCaptureIntruders(enable: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_CAPTURE_INTRUDERS, enable).apply()
    }

    fun setIndexMultipleTabs(index: Int) {
        sharedPreferences.edit().putInt(KEY_ID_MULTIPLE_TABS, index).apply()
    }

    fun getIndexMultipleTabs(): Int {
        return sharedPreferences.getInt(KEY_ID_MULTIPLE_TABS, 0)
    }

    fun getImageDefault(): String {
        return sharedPreferences.getString(KEY_IMAGE_DEFAULT, "") ?: ""
    }

    fun setImageDefault(value: String) {
        sharedPreferences.edit().putString(KEY_IMAGE_DEFAULT, value).apply()
    }

    fun saveTheme(themeId: Int) {
        sharedPreferences.edit().putInt(KEY_CURRENT_THEME, themeId).apply()
    }

    fun getThemeId(): Int {
        return sharedPreferences.getInt(KEY_CURRENT_THEME, AppTheme.DEFAULT.themeId)
    }

    fun saveCustomTheme(themeId: Int) {
        sharedPreferences.edit().putInt(KEY_CUSTOM_THEME, themeId).apply()
    }

    fun getCustomThemeId(): Int {
        return sharedPreferences.getInt(KEY_CUSTOM_THEME, -1)
    }

    fun isAppLockEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_APPLOCK_ENABLE, true)
    }

    fun setAppLockEnable(enable: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_APPLOCK_ENABLE, enable).apply()
    }

    fun isVibrationEnable(): Boolean {
        return sharedPreferences.getBoolean(KEY_VIBRATION, true)
    }

    fun setVibrationEnable(enable: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_VIBRATION, enable).apply()
    }

    fun isLockNewAppEnable(): Boolean {
        return sharedPreferences.getBoolean(KEY_LOCK_NEW_APP, true)
    }

    fun setLockNewAppEnable(enable: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_LOCK_NEW_APP, enable).apply()
    }

    fun isUnInstallEnable(): Boolean {
        return sharedPreferences.getBoolean(KEY_UNINSTALL, false)
    }

    fun setUnInstallEnable(enable: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_UNINSTALL, enable).apply()
    }

    fun getLanguageCode(): String {
        return sharedPreferences.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun setLanguageCode(value: String) {
        sharedPreferences.edit().putString(KEY_LANGUAGE, value).apply()
    }

    fun setSecurityQuestionEnable(checked: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_SECURITY_QUESTION, checked).apply()
    }

    fun getSecurityQuestionEnable(): Boolean {
        return sharedPreferences.getBoolean(KEY_SECURITY_QUESTION, false)
    }

    fun setSecurityQuestionId(questionId: Int) {
        sharedPreferences.edit().putInt(KEY_QUESTION_ID, questionId).apply()
    }

    fun getSecurityQuestionId(): Int {
        return sharedPreferences.getInt(KEY_QUESTION_ID, 0)
    }

    fun setAnswer(answer: String) {
        sharedPreferences.edit().putString(KEY_ANSWER, answer).apply()
    }

    fun getAnswer(): String {
        return sharedPreferences.getString(KEY_ANSWER, "") ?: ""
    }

    fun setCleanFirst(value: Boolean) {
        sharedPreferences.edit().putBoolean(CLEAN_FIRST, value).apply()
    }

    fun getCleanFirst(): Boolean {
        return sharedPreferences.getBoolean(CLEAN_FIRST, false)
    }

    companion object {
        const val APP_SHARE_KEY = "APP_LOCK"
        const val KEY_KNOCK_CODE = "KEY_KNOCK_CODE"
        const val KEY_PATTERN = "PATTERN"
        const val KEY_PIN = "PIN"
        private const val KEY_LOCK_FREQUENCY_TYPE = "LOCK_FREQUENCY_TYPE"
        private const val KEY_UNLOCK_ANIMATION = "UNLOCK_ANIMATION"
        private const val KEY_RANDOM_KEYBOARD = "RANDOM_KEYBOARD"
        private const val KEY_HIDE_PATTERN_TRAILS = "HIDE_PATTERN_TRAILS"
        private const val KEY_LOCK_TYPE = "LOCK_TYPE"
        private const val KEY_FINGERPRINT_ENABLE = "FINGERPRINT_ENABLE"
        private const val KEY_RECOMMEND_SIGNALS = "RECOMMEND_SIGNALS"
        private const val KEY_FAKE_COVER = "FAKE_COVER"
        private const val KEY_ID_MULTIPLE_TABS = "KEY_ID_MULTIPLE_TABS"
        private const val KEY_IMAGE_DEFAULT = "KEY_IMAGE_DEFAULT"

        private const val KEY_UNLOCK_COUNT = "KEY_UNLOCK_COUNT"
        private const val KEY_CAPTURE_INTRUDERS = "KEY_CAPTURE_INTRUDERS"
        private const val KEY_CURRENT_THEME = "CURRENT_THEME"
        private const val KEY_CUSTOM_THEME = "CUSTOM_THEME"

        private const val KEY_APPLOCK_ENABLE = "APPLOCK_ENABLE"
        private const val KEY_VIBRATION = "VIBRATION"
        private const val KEY_LOCK_NEW_APP = "LOCK_NEW_APP"
        private const val KEY_UNINSTALL = "UNINSTALL"
        private const val KEY_LANGUAGE = "LANGUAGE"
        private const val KEY_SECURITY_QUESTION = "SECURIRTY_QUESTION"
        private const val KEY_QUESTION_ID = "QUESTION_ID"
        private const val KEY_ANSWER = "ANSWER"
        private const val ERROR_FIRST = "ERROR_FIRST"
        private const val TIME_UN_LOCK = "TIME_UN_LOCK"
        private const val CLEAN_FIRST = "CLEAN_FIRST"
    }
}