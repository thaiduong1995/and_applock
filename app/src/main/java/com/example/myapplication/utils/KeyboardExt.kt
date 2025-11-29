package com.example.myapplication.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun Context.showKeyboard(edt: EditText) {
    edt.requestFocus()
    getSystemService(InputMethodManager::class.java).showSoftInput(
        edt,
        InputMethodManager.SHOW_IMPLICIT
    )
}

fun Context.showDelayedKeyboard(edt: EditText, delayedTime: Long = 500) {
    Handler(Looper.getMainLooper()).postDelayed({
        showKeyboard(edt)
    }, delayedTime)
}
