package com.example.myapplication.view_model

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.base.BaseViewModel
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.model.AppTheme
import com.example.myapplication.data.model.CustomTheme
import com.example.myapplication.data.model.LockType
import com.example.myapplication.data.model.ThemePreview
import com.example.myapplication.data.model.ThemeTopic
import com.example.myapplication.data.model.liveData.MutableStateLiveData
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.PreferenceHelper
import com.example.myapplication.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preference: PreferenceHelper
) : BaseViewModel() {

    val themePreviewLiveData = MutableStateLiveData<ArrayList<ThemePreview>>()
    val saveThemeStateLiveData = MutableStateLiveData<Boolean>()

    var customThemeLiveData: LiveData<List<CustomTheme>>? = null
    var listCustomTheme: List<CustomTheme> = arrayListOf()
    var getThemeId = MutableStateLiveData<Int>()

    fun getDetailsTopic(themeTopic: ThemeTopic) {
        val themeList = ArrayList<ThemePreview>()
        viewModelScope.launch(Dispatchers.IO) {
            val data = AppTheme.values().filter { it.themePath.contains(themeTopic.folderName) }
            data.forEach {
                val theme = Utils.getThemePreview(it)
                themeList.add(theme)
            }
            themePreviewLiveData.postSuccess(themeList)
        }
    }

    fun saveTheme(currentTheme: ThemePreview) {
        preference.saveTheme(currentTheme.themeId)
        preference.saveCustomTheme(-1)
    }

    fun getTheme() {
        getThemeId.postSuccess(preference.getThemeId())
    }

    fun getLockType(): Int {
        return preference.getLockType()
    }

    fun getCurrentCustomTheme(): CustomTheme? {
        val currentCustomThemeId = preference.getCustomThemeId()
        listCustomTheme =
            AppDatabase.getInstance(context).getCustomThemeDao().getAllCustomThemeSync()
        return listCustomTheme.find { it.id == currentCustomThemeId }
    }


    fun saveCustomTheme(customTheme: CustomTheme) {
        //reset asset theme to default
        preference.saveTheme(Constants.DEFAULT_THEME)
        preference.saveCustomTheme(customTheme.id)
    }

    fun addCustomTheme(
        backgroundBitmap: Bitmap,
        previewBitmap: Bitmap,
        lockType: LockType,
        dotsColor: Int,
        numberColor: Int,
        knockColor: Int,
        lineColor: Int
    ) {
        saveThemeStateLiveData.postLoading()
        viewModelScope.launch(Dispatchers.IO) {
            var backgroundPath: String?
            backgroundBitmap.let {
                backgroundPath = saveBitmapToInternalStorage(
                    context, it, System.currentTimeMillis().toString().plus(".png")
                )
            }
            val previewPath = saveBitmapToInternalStorage(
                context, previewBitmap, System.currentTimeMillis().toString().plus(".png")
            )

            backgroundPath?.let {
                val customTheme = CustomTheme(
                    backgroundImagePath = it,
                    previewImagePath = previewPath,
                    dotColor = dotsColor,
                    numberColor = numberColor,
                    knockColor = knockColor,
                    lineColor = lineColor,
                    lockType = lockType.id
                )
                AppDatabase.getInstance(context).getCustomThemeDao().insert(customTheme)
            }

            saveThemeStateLiveData.postSuccess(true)
        }
    }


    private fun saveBitmapToInternalStorage(
        context: Context, bitmap: Bitmap, fileName: String
    ): String {
        val directory = context.filesDir
        val file = File(directory, fileName)

        return try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            "null"
        }
    }

    fun deleteCustomTheme(customTheme: CustomTheme) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance(context).getCustomThemeDao().delete(customTheme.id)
            val customThemeId = preference.getCustomThemeId()
            if (customTheme.id == customThemeId) {
                preference.saveCustomTheme(-1)
            }
        }
    }

    init {
        customThemeLiveData =
            AppDatabase.getInstance(context).getCustomThemeDao().getAllCustomTheme()
        viewModelScope.launch(Dispatchers.IO) {
            listCustomTheme =
                AppDatabase.getInstance(context).getCustomThemeDao().getAllCustomThemeSync()
        }
    }
}