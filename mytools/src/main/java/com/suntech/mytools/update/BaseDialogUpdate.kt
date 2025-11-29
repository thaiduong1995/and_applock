package com.suntech.mytools.update

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.suntech.mytools.R


open class BaseDialogUpdate : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun getTheme() = R.style.RoundedCornersDialog
}