package com.example.myapplication.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.data.model.OverlayValidateType
import com.example.myapplication.data.model.Password
import com.example.myapplication.databinding.FragmentSecurityBinding
import com.example.myapplication.extention.FirebaseEvent
import com.example.myapplication.extention.getNameAnimation
import com.example.myapplication.extention.getNameLockFrequency
import com.example.myapplication.extention.isHaveBiometric
import com.example.myapplication.extention.isHaveFingerPrint
import com.example.myapplication.extention.parcelable
import com.example.myapplication.extention.toastMessageShortTime
import com.example.myapplication.ui.dialog.CommonSelectorDialog
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.Constants.KEY_TYPE_PASSWORD
import com.example.myapplication.view_model.MainViewModel
import com.example.myapplication.view_model.SecurityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecurityFragment : BaseCacheFragment<FragmentSecurityBinding>() {

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val viewModel by viewModels<SecurityViewModel>()

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentSecurityBinding {
        return FragmentSecurityBinding.inflate(inflater, container, false)
    }

    override fun initUI() {
        context?.let { ct ->
            binding.apply {
                when (viewModel.preference.getLockType()) {
                    OverlayValidateType.TYPE_PATTERN.value -> cbPatternLock.isChecked = true
                    OverlayValidateType.TYPE_PIN.value -> cbPinLock.isChecked = true
                    OverlayValidateType.TYPE_KNOCK_CODE.value -> cbKnockCode.isChecked = true
                }

                val lockFrequencyType = viewModel.preference.getLockFrequencyType()
                tvRelockTime.text = ct.getString(getNameLockFrequency(lockFrequencyType))

                val animation = viewModel.preference.getAnimation()
                tvAnimation.text = ct.getString(getNameAnimation(animation))

                swKeyboardRandom.isChecked = viewModel.preference.getRandomKeyboard()
                swHidePatternTrails.isChecked = viewModel.preference.isHidePatternTrails()

                swFingerPrint.isChecked = viewModel.preference.isEnableFingerPrint()

                if (mainViewModel.isSetupPatternCode()) {
                    binding.tvChangepattern.text = getString(R.string.reset_pattern)
                } else {
                    binding.tvChangepattern.text = getString(R.string.create_pattern)
                }

                if (mainViewModel.isSetupKnockCode()) {
                    binding.tvCreateKnockCode.text = getString(R.string.reset_knock_code)
                } else {
                    binding.tvCreateKnockCode.text = getString(R.string.create_your_knock_code)
                }

                if (mainViewModel.isSetupPinCode()) {
                    binding.tvCreatePin.text = getString(R.string.reset_pin_code)
                } else {
                    binding.tvCreatePin.text = getString(R.string.create_pin)
                }
                val isHaveBiometricOrFingerPrint =
                    activity?.isHaveBiometric() == true || activity?.isHaveFingerPrint() == true
                binding.layoutFingerPrint.isInvisible = !isHaveBiometricOrFingerPrint
            }
        }
    }

    override fun initListener() {
        binding.layoutKnockCode.setOnClickListener(this@SecurityFragment)
        binding.layoutFingerPrint.setOnClickListener(this@SecurityFragment)
        binding.layoutPin.setOnClickListener(this@SecurityFragment)
        binding.layoutPattern.setOnClickListener(this@SecurityFragment)
        binding.layoutRelockSetting.setOnClickListener(this)
        binding.layoutUnlockAnimation.setOnClickListener(this)
        binding.swKeyboardRandom.setOnClickListener(this)
        binding.swHidePatternTrails.setOnClickListener(this)
        binding.layoutChangePattern.setOnClickListener(this)
        binding.layoutCreatePin.setOnClickListener(this)
        binding.layoutCreateKnockCode.setOnClickListener(this)
    }

    override fun initData() {
        FirebaseEvent.viewSecurity()
    }

    override fun initObservers() {
        setFragmentResultListener(Constants.KEY_RESULT_TO_SECURITY_FRAGMENT) { key, bundle ->
            if (key == Constants.KEY_RESULT_TO_SECURITY_FRAGMENT) {
                val resultData = bundle.parcelable<Password>(Constants.KEY_RESULT_DATA)
                when (resultData?.id) {
                    Constants.TYPE_PIN_4_digit, Constants.TYPE_PIN_6_digit -> {
                        binding.cbPatternLock.isChecked = false
                        binding.cbPinLock.isChecked = true
                        binding.cbKnockCode.isChecked = false
                        FirebaseEvent.clickSettingsLock(OverlayValidateType.TYPE_PIN.name)
                        viewModel.preference.setLockType(OverlayValidateType.TYPE_PIN.value)
                    }

                    Constants.TYPE_KNOCK_CODE -> {
                        binding.cbPatternLock.isChecked = false
                        binding.cbPinLock.isChecked = false
                        binding.cbKnockCode.isChecked = true
                        FirebaseEvent.clickSettingsLock(OverlayValidateType.TYPE_KNOCK_CODE.name)
                        viewModel.preference.setLockType(OverlayValidateType.TYPE_KNOCK_CODE.value)
                    }

                    Constants.TYPE_PATTERN -> {
                        binding.cbPatternLock.isChecked = true
                        binding.cbPinLock.isChecked = false
                        binding.cbKnockCode.isChecked = false
                        FirebaseEvent.clickSettingsLock(OverlayValidateType.TYPE_PATTERN.name)
                        viewModel.preference.setLockType(OverlayValidateType.TYPE_PATTERN.value)
                    }
                }

                if (mainViewModel.isSetupPatternCode()) {
                    binding.tvChangepattern.text = getString(R.string.reset_pattern)
                } else {
                    binding.tvChangepattern.text = getString(R.string.create_pattern)
                }

                if (mainViewModel.isSetupKnockCode()) {
                    binding.tvCreateKnockCode.text = getString(R.string.reset_knock_code)
                } else {
                    binding.tvCreateKnockCode.text = getString(R.string.create_your_knock_code)
                }

                if (mainViewModel.isSetupPinCode()) {
                    binding.tvCreatePin.text = getString(R.string.reset_pin_code)
                } else {
                    binding.tvCreatePin.text = getString(R.string.create_pin)
                }
            }
        }
    }

    override fun onViewClicked(view: View) {
        when (view.id) {
            R.id.layoutPattern -> {
                if (viewModel.preference.getPatternCode().isEmpty()) {
                    navigateToCreatePassword(Constants.TYPE_PATTERN)
                } else {
                    binding.cbPatternLock.isChecked = true
                    binding.cbPinLock.isChecked = false
                    binding.cbKnockCode.isChecked = false
                    context?.let {
                        viewModel.preference.setLockType(OverlayValidateType.TYPE_PATTERN.value)
                    }
                }
                FirebaseEvent.chooseLock(OverlayValidateType.TYPE_PATTERN.name)
            }

            R.id.layoutPin -> {
                if (viewModel.preference.getPinCode().isEmpty()) {
                    navigateToCreatePassword(Constants.TYPE_PIN_4_digit)
                } else {
                    binding.cbPatternLock.isChecked = false
                    binding.cbPinLock.isChecked = true
                    binding.cbKnockCode.isChecked = false
                    context?.let {
                        viewModel.preference.setLockType(
                            OverlayValidateType.TYPE_PIN.value
                        )
                    }
                }
                FirebaseEvent.chooseLock(OverlayValidateType.TYPE_PIN.name)
            }

            R.id.layoutKnockCode -> {
                if (viewModel.preference.getKnockCode().isEmpty()) {
                    navigateToCreatePassword(Constants.TYPE_KNOCK_CODE)
                } else {
                    binding.cbPatternLock.isChecked = false
                    binding.cbPinLock.isChecked = false
                    binding.cbKnockCode.isChecked = true
                    context?.let {
                        context?.let {
                            viewModel.preference.setLockType(
                                OverlayValidateType.TYPE_KNOCK_CODE.value
                            )
                        }
                    }
                }
                FirebaseEvent.chooseLock(OverlayValidateType.TYPE_KNOCK_CODE.name)
            }

            R.id.layoutFingerPrint -> {
                FirebaseEvent.chooseLock("TYPE_FINGER_PRINT")
                val isHaveBiometricOrFingerPrint =
                    activity?.isHaveBiometric() == true || activity?.isHaveFingerPrint() == true
                val enable = !viewModel.preference.isEnableFingerPrint()
                if (!isHaveBiometricOrFingerPrint) {
                    context?.toastMessageShortTime(getString(R.string.biometric_unavailable))
                    return
                }
                viewModel.preference.setEnableFingerPrint(enable)
                binding.swFingerPrint.isChecked = enable
            }

            R.id.layoutRelockSetting -> {
                showDialogSelectLockFrequency()
            }

            R.id.layoutUnlockAnimation -> {
                showDialogSelectAnimation()
            }

            R.id.swKeyboardRandom -> {
                val value = !viewModel.preference.getRandomKeyboard()
                viewModel.preference.setRandomKeyboard(value)
                binding.swKeyboardRandom.isChecked = value
            }

            R.id.swHidePatternTrails -> {
                val value = !viewModel.preference.isHidePatternTrails()
                viewModel.preference.setHidePatternTrails(value)
                binding.swHidePatternTrails.isChecked = value
            }

            R.id.layoutChangePattern -> {
                navigateToCreatePassword(Constants.TYPE_PATTERN)
            }

            R.id.layoutCreatePin -> {
                navigateToCreatePassword(Constants.TYPE_PIN_4_digit)
            }

            R.id.layoutCreateKnockCode -> {
                navigateToCreatePassword(Constants.TYPE_KNOCK_CODE)
            }
        }
    }

    private fun navigateToCreatePassword(type: Int) {
        findNavController().navigate(
            R.id.createPasswordScreen, bundleOf(
                KEY_TYPE_PASSWORD to type
            )
        )
    }

    private fun showDialogSelectLockFrequency() {
        viewModel.listLockFrequencyLiveData.value?.let {
            val dialog = CommonSelectorDialog.newInstance(getString(R.string.lock_frequency))
            if (!dialog.isShown) {
                dialog.show(childFragmentManager, "")
            }
            dialog.onClickEvent = {
                binding.tvRelockTime.text =
                    it.idString?.let { it1 -> requireContext().getString(it1) }
                viewModel.saveFrequencyToLocal(it)
            }
            dialog.setData(it)
        }
    }

    private fun showDialogSelectAnimation() {
        viewModel.listAnimationLiveData.value?.let {
            val dialog = CommonSelectorDialog.newInstance(getString(R.string.unlock_animation))
            if (!dialog.isShown) {
                dialog.show(parentFragmentManager, "")
            }
            dialog.onClickEvent = {
                binding.tvAnimation.text =
                    it.idString?.let { it1 -> requireContext().getString(it1) }
                viewModel.saveAnimationToLocal(it)
            }
            dialog.setData(it)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = SecurityFragment().apply {
            arguments = bundleOf()
        }
    }
}