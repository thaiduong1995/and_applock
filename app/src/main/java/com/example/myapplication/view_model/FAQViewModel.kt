package com.example.myapplication.view_model

import com.example.myapplication.R
import com.example.myapplication.base.BaseViewModel
import com.example.myapplication.data.model.Question
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FAQViewModel @Inject constructor() : BaseViewModel() {

    val questions = arrayListOf(
        Question(
            icon = R.drawable.ic_request_access,
            question = R.string.how_to_set_pin_code,
            description = R.string.how_to_set_pin_code_description
        ),
        Question(
            icon = R.drawable.ic_faq_fingerprint_enable,
            question = R.string.how_to_enable_finger_print,
            description = R.string.how_to_enable_finger_print_description
        ),
        Question(
            icon = R.drawable.ic_faq_fingerprint,
            question = R.string.how_to_disable_finger_print,
            description = R.string.how_to_disable_finger_print_description
        ),
        Question(
            icon = R.drawable.ic_faq_change_pin_code,
            question = R.string.how_to_change_pin_code,
            description = R.string.how_to_change_pin_code_description
        ),
        Question(
            icon = R.drawable.ic_faq_uninstall_protection,
            question = R.string.how_to_uninstall_protection,
            description = R.string.how_to_uninstall_protection_description
        ),
        Question(
            icon = R.drawable.ic_faq_relock_setting,
            question = R.string.how_to_relock_setting,
            description = R.string.how_to_relock_setting_description
        ),
        Question(
            icon = R.drawable.ic_fqa_fake_icon,
            question = R.string.how_to_fake_icon,
            description = R.string.how_to_fake_icon_description
        ),
    )
}