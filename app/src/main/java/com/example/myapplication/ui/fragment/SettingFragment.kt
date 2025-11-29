package com.example.myapplication.ui.fragment

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Context.DEVICE_POLICY_SERVICE
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.databinding.FragmentSettingBinding
import com.example.myapplication.extention.FirebaseEvent
import com.example.myapplication.service.AppLockDeviceAdminReceiver
import com.example.myapplication.service.ServiceStarter
import com.example.myapplication.ui.dialog.DialogLanguageOption
import com.example.myapplication.ui.dialog.DialogRequestProtection
import com.example.myapplication.ui.dialog.DialogSecurityQuestion
import com.example.myapplication.ui.dialog.DialogTurnOffProtection
import com.example.myapplication.utils.IntentHelper
import com.example.myapplication.utils.setSingleClick
import com.example.myapplication.view_model.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : BaseCacheFragment<FragmentSettingBinding>() {

    private val viewModel: SettingViewModel by viewModels()

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentSettingBinding {
        return FragmentSettingBinding.inflate(inflater, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun initData() {
        super.initData()
        FirebaseEvent.viewSettings()
    }

    override fun initUI() {
        binding.tvLanguage.text = viewModel.getCurrentLanguage()
        binding.swSettingAppLock.isChecked = viewModel.isAppLockEnabled()
        binding.swSettingVibration.isChecked = viewModel.isVibrationEnable()
        binding.swSettingLockNewApp.isChecked = viewModel.isLockNewAppEnable()
        binding.swSettingSecurityQuestion.isChecked = viewModel.getSecurityQuestionEnable()
        binding.swSettingUninstallProtection.isChecked =
            devicePolicyManager.isAdminActive(adminComponentName)

        binding.tvShowSecurityQuestion.setTextColor(
            if (viewModel.getSecurityQuestionEnable()) ContextCompat.getColor(
                requireContext(), R.color.white
            ) else ContextCompat.getColor(requireContext(), R.color.gray666666)
        )
        binding.imgShowSQ.imageTintList =
            (if (viewModel.getSecurityQuestionEnable()) ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(), R.color.white
                )
            ) else ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(), R.color.gray666666
                )
            ))
    }

    override fun initListener() {
        super.initListener()
        binding.swSettingUninstallProtection.setOnClickListener {
            FirebaseEvent.clickScreenSettings(FirebaseEvent.SWITCH_UNINSTALL_PROTECTION)
            binding.swSettingUninstallProtection.isChecked =
                !binding.swSettingUninstallProtection.isChecked
            if (binding.swSettingUninstallProtection.isChecked) {
                showDialogUninstallProtectionOff()
            } else {
                showDialogUninstallProtectionOn()
            }
        }

        binding.swSettingAppLock.setOnCheckedChangeListener { buttonView, isChecked ->
            FirebaseEvent.clickScreenSettings(FirebaseEvent.SWITCH_LOCK)
            viewModel.setEnableAppLock(isChecked)
            if (!isChecked) {
                ServiceStarter.stopLockService(requireContext())
            } else {
                ServiceStarter.restartService(requireContext())
            }
        }


        binding.swSettingVibration.setOnCheckedChangeListener { buttonView, isChecked ->
            FirebaseEvent.clickScreenSettings(FirebaseEvent.SWITCH_VIBRATION)
            viewModel.setVibrationEnable(isChecked)
        }

        binding.swSettingLockNewApp.setOnCheckedChangeListener { buttonView, isChecked ->
            FirebaseEvent.clickScreenSettings(FirebaseEvent.SWITCH_LOCK_NEW)
            viewModel.setLockNewAppEnable(isChecked)
        }

        binding.swSettingSecurityQuestion.setOnCheckedChangeListener { buttonView, isChecked ->
            FirebaseEvent.clickScreenSettings(FirebaseEvent.SWITCH_QUESTION)
            viewModel.setSecurityQuestionEnable(isChecked)
            changeStateButtonSecurityQuestion(isChecked)
            if (viewModel.isGetValueBoolean()) {
                val dialog = DialogSecurityQuestion.newInstance(viewModel.listQuestion.toTypedArray())
                dialog.onConfirm = {
                    binding.swSettingSecurityQuestion.isChecked = it
                    viewModel.setSecurityQuestionEnable(it)
                }
                dialog.show(childFragmentManager, "")
            }
        }

        binding.btnLanguage.setOnClickListener {
            showDialogLanguageOption()
        }

        binding.btnFAQ.setOnClickListener {
            FirebaseEvent.clickScreenSettings(FirebaseEvent.CLICK_FAQ)
            findNavController().navigate(R.id.faqScreen)
        }

        binding.btnShowSQ.setOnClickListener {
            if (viewModel.getSecurityQuestionEnable()) {
                val dialog = DialogSecurityQuestion.newInstance(viewModel.listQuestion.toTypedArray())
                dialog.show(parentFragmentManager, "")
            }
        }

        binding.imgBanner.setOnClickListener {
            FirebaseEvent.clickScreenSettings(FirebaseEvent.CLICK_BANNER_PRO)
        }

        binding.btnShare.setSingleClick {
            FirebaseEvent.clickScreenSettings(FirebaseEvent.CLICK_SHARE)
            context?.startActivity(IntentHelper.shareIntent(requireContext()))
        }

        binding.btnRateUs.setSingleClick {
            FirebaseEvent.clickScreenSettings(FirebaseEvent.CLICK_RATE)
            context?.startActivity(IntentHelper.rateUsIntent(requireContext()))
        }

        binding.btnPrivacyPolicy.setOnClickListener {
            context?.startActivity(IntentHelper.privacyTermWebIntent())
        }

        binding.btnRateUs.setOnClickListener {
            context?.startActivity(IntentHelper.rateUsIntent(requireContext()))
        }
        binding.btnFeedback.setOnClickListener {
            startActivity(Intent.createChooser(IntentHelper.sendMailIntent(), "Send mail..."))
        }

        binding.imgBanner.setOnClickListener {
            findNavController().navigate(R.id.purchaseScreen)
        }
    }

    private fun changeStateButtonSecurityQuestion(checked: Boolean) {
        binding.tvShowSecurityQuestion.setTextColor(
            if (checked) ContextCompat.getColor(
                requireContext(), R.color.white
            ) else ContextCompat.getColor(requireContext(), R.color.gray666666)
        )
        binding.imgShowSQ.imageTintList = (if (checked) ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(), R.color.white
            )
        ) else ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray666666)))
    }

    private fun showDialogUninstallProtectionOn() {
        val dialog = DialogRequestProtection.newInstance()
        dialog.onClickEvent = {
            if (!devicePolicyManager.isAdminActive(adminComponentName)) {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponentName)
                intent.putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    getString(R.string.need_request_access)
                )
                launcher.launch(intent)
            }
            binding.swSettingUninstallProtection.isChecked =
                devicePolicyManager.isAdminActive(adminComponentName)
        }
        dialog.show(childFragmentManager, "DialogRequestProtection")
    }

    private fun showDialogUninstallProtectionOff() {
        val dialog = DialogTurnOffProtection.newInstance()
        if (!dialog.isShown) {
            dialog.show(childFragmentManager, "")
        }
        dialog.onClickEvent = {
            devicePolicyManager.removeActiveAdmin(adminComponentName)
            binding.swSettingUninstallProtection.isChecked = false
        }
    }

    private fun showDialogLanguageOption() {
        viewModel.getListLanguage().let {
            val dialog = DialogLanguageOption.newInstance(it.toTypedArray())
            dialog.show(parentFragmentManager, "")

            dialog.onClickEvent = { item ->
                changeLanguage()
                viewModel.saveLanguageCode(item.code)
            }
        }
    }

    private fun changeLanguage() {
        val intent = activity?.intent
        activity?.overridePendingTransition(0, 0)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        activity?.finish()
        activity?.overridePendingTransition(0, 0)
        if (intent != null) {
            startActivity(intent)
        }
    }

    private val devicePolicyManager by lazy {
        requireContext().getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
    }
    private val adminComponentName by lazy {
        ComponentName(requireContext(), AppLockDeviceAdminReceiver::class.java)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            binding.swSettingUninstallProtection.isChecked =
                devicePolicyManager.isAdminActive(adminComponentName)
        }

    override fun onDetach() {
        super.onDetach()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onResume() {
        super.onResume()
        //binding.imgBanner.isGone = DataLocal.isVip()
    }

    companion object {

        @JvmStatic
        fun newInstance() = SettingFragment().apply {
            arguments = bundleOf()
        }
    }
}