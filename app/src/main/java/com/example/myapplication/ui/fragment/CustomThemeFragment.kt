package com.example.myapplication.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.data.model.LockType
import com.example.myapplication.databinding.FragmentCustomThemeBinding
import com.example.myapplication.extention.FirebaseEvent
import com.example.myapplication.extention.selected
import com.example.myapplication.extention.toastMessageShortTime
import com.example.myapplication.utils.Constants

class CustomThemeFragment : BaseCacheFragment<FragmentCustomThemeBinding>() {

    private var currentType: LockType? = null

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCustomThemeBinding {
        return FragmentCustomThemeBinding.inflate(inflater, container, false)
    }

    override fun initListener() {
        binding.imgBack.setOnClickListener {
            popBackStack()
        }

        binding.layoutPattern.setOnClickListener {
            currentType = LockType.PATTERN
            binding.layoutPattern.selected(true)
            binding.layoutKnockCode.selected(false)
            binding.layoutPin.selected(false)
        }

        binding.layoutPin.setOnClickListener {
            currentType = LockType.PASS_CODE
            binding.layoutPattern.selected(false)
            binding.layoutKnockCode.selected(false)
            binding.layoutPin.selected(true)
        }

        binding.layoutKnockCode.setOnClickListener {
            currentType = LockType.KNOCK
            binding.layoutPattern.selected(false)
            binding.layoutKnockCode.selected(true)
            binding.layoutPin.selected(false)
        }

        binding.btnCustomTheme.setOnClickListener {
            if (currentType == null) {
                context?.toastMessageShortTime(getString(R.string.you_need_to_select_type))
            } else {
                when (currentType) {
                    LockType.PATTERN -> FirebaseEvent.clickCustom(FirebaseEvent.CLICK_CUSTOM_PATTERN)
                    LockType.PASS_CODE -> FirebaseEvent.clickCustom(FirebaseEvent.CLICK_CUSTOM_PIN)
                    LockType.KNOCK -> FirebaseEvent.clickCustom(FirebaseEvent.CLICK_CUSTOM_KNOCK)
                    else -> {}
                }
                findNavController().navigate(
                    R.id.detailsCustomThemeScreen,
                    bundleOf(Constants.KEY_TYPE_PASSWORD to currentType)
                )
            }
        }
    }
}
