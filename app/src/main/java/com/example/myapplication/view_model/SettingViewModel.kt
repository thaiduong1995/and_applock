package com.example.myapplication.view_model

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import com.example.myapplication.R
import com.example.myapplication.base.BaseViewModel
import com.example.myapplication.data.model.LANGUAGEType
import com.example.myapplication.data.model.Language
import com.example.myapplication.data.model.SecurityQuestion
import com.example.myapplication.utils.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    val preference: PreferenceHelper
) : BaseViewModel() {

    var listLanguageLiveData = MediatorLiveData<List<Language>>()

    fun isAppLockEnabled(): Boolean {
        return preference.isAppLockEnabled()
    }

    fun setEnableAppLock(enable: Boolean) {
        preference.setAppLockEnable(enable)
    }

    fun isVibrationEnable(): Boolean {
        return preference.isVibrationEnable()
    }

    fun setVibrationEnable(enable: Boolean) {
        preference.setVibrationEnable(enable)
    }

    fun isLockNewAppEnable(): Boolean {
        return preference.isLockNewAppEnable()
    }

    fun setLockNewAppEnable(enable: Boolean) {
        preference.setLockNewAppEnable(enable)
    }

    fun isUnInstallEnable(): Boolean {
        return preference.isUnInstallEnable()
    }

    fun getListLanguage(): ArrayList<Language> {
        val currentLanguageCode = preference.getLanguageCode()
        val listLanguage = arrayListOf<Language>()
        listLanguage.addAll(
            LANGUAGEType.values().toList().map { Language(it.languageName, it.languageCode) })
        listLanguage.find { it.code == currentLanguageCode }?.isSelected = true
        if (listLanguage.filter { it.isSelected }.isEmpty()) {
            listLanguage[0].isSelected = true
        }
        return listLanguage
    }

    fun saveLanguageCode(languageCode: String) {
        preference.setLanguageCode(languageCode)
    }

    fun getCurrentLanguage(): String {
        val currentLanguageCode = preference.getLanguageCode()
        return LANGUAGEType.values().toList().filter { it.languageCode == currentLanguageCode }
            .firstOrNull()?.languageName ?: context.getString(R.string.english)
    }

    fun setSecurityQuestionEnable(checked: Boolean) {
        preference.setSecurityQuestionEnable(checked)
    }

    fun getSecurityQuestionEnable(): Boolean {
        return preference.getSecurityQuestionEnable()
    }

    fun setSecurityQuestionId(questionId: Int) {
        preference.setSecurityQuestionId(questionId)
    }

    val listQuestion = arrayListOf<SecurityQuestion>().apply {
        add(SecurityQuestion(R.string.question_favorite_color, true))
        add(SecurityQuestion(R.string.question_pet_name, false))
        add(SecurityQuestion(R.string.question_lucky_number, false))
    }

    fun isGetValueBoolean(): Boolean {
        return preference.getSecurityQuestionEnable() && preference.getSecurityQuestionId() == 0
    }
}

