package com.example.myapplication.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.data.model.Password
import com.example.myapplication.data.model.Step
import com.example.myapplication.data.model.ThemeData
import com.example.myapplication.databinding.FragmentSetupBinding
import com.example.myapplication.extention.FirebaseEvent
import com.example.myapplication.extention.toIntArray
import com.example.myapplication.ui.custom.passwordView.InputPasswordListener
import com.example.myapplication.ui.dialog.DialogSelectTypePassword
import com.example.myapplication.utils.Constants
import com.example.myapplication.view_model.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SetupFragment : BaseCacheFragment<FragmentSetupBinding>() {

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val listPasswordTypes = arrayListOf<Password>()
    private var currentType: Password? = null
    private var step: Step = Step.Step1
    private var password: String = ""
    private var confirmPassword: String = ""

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): FragmentSetupBinding {
        return FragmentSetupBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseEvent.logViewCreateScreen()
        initOverlayView()
    }

    private fun initOverlayView() {
        binding.layoutPatternCode.patternView.setInputPasswordListener(object :
            InputPasswordListener {
            override fun onStartInput() {
                Timber.e("onStartInput")
                binding.tvMessage.visibility = View.GONE
            }

            override fun onInputComplete(password: String) {
                if (password != "" && password.toIntArray().size >= Constants.MIN_PASSWORD_LENGTH) {
                    binding.layoutPatternCode.patternView.disableTouch()
                    if (step == Step.Step1) {
                        this@SetupFragment.password = password
                        binding.btnCreate.visibility = View.VISIBLE
                    } else {
                        confirmPassword = password
                        binding.btnConfirm.visibility = View.VISIBLE
                    }
                } else {
                    binding.tvMessage.visibility = View.VISIBLE
                    binding.tvMessage.text = getString(R.string.password_length_error)
                    binding.layoutPatternCode.patternView.reset()
                }
                binding.layoutPatternCode.patternView.setSateError()
            }

            override fun onInputting() {
                Timber.e("onInputting")
            }
        })

        binding.layoutPinCode.pinCodeView.setInputPasswordListener(object : InputPasswordListener {
            override fun onStartInput() {
                binding.tvMessage.visibility = View.GONE
            }

            override fun onInputComplete(pinCode: String) {
                if (pinCode.length == currentType?.passwordLength) {
                    if (step == Step.Step1) {
                        password = pinCode
                        binding.btnCreate.visibility = View.VISIBLE
                    } else {
                        confirmPassword = pinCode
                        binding.btnConfirm.visibility = View.VISIBLE
                    }
                }
            }

            override fun onInputting() {
            }
        })
        binding.layoutKnockCode.knockView.setInputPasswordListener(object : InputPasswordListener {
            override fun onStartInput() {
                binding.tvMessage.visibility = View.GONE
            }

            override fun onInputComplete(inputPassword: String) {
                if (inputPassword.toList().map { it.code }
                        .filter { it > 0 }.size >= Constants.MIN_PASSWORD_LENGTH) {
                    if (step == Step.Step1) {
                        password = inputPassword
                        binding.btnCreate.visibility = View.VISIBLE
                    } else {
                        confirmPassword = inputPassword
                        binding.btnConfirm.visibility = View.VISIBLE
                    }
                }

                binding.tvMessage.visibility = View.GONE
            }

            override fun onInputting() {}
        })

    }

    override fun initData() {
        listPasswordTypes.add(
            Password(
                Constants.TYPE_PIN_4_digit,
                getString(R.string.four_digit_pin),
                R.drawable.ic_six_digit_pin,
                4,
                true
            )
        )
        listPasswordTypes.add(
            Password(
                Constants.TYPE_PIN_6_digit,
                getString(R.string.six_digit_pin),
                R.drawable.ic_six_digit_pin,
                6
            )
        )
        listPasswordTypes.add(
            Password(
                Constants.TYPE_PATTERN, getString(R.string.pattern), R.drawable.ic_pattern, 6
            )
        )
        listPasswordTypes.add(
            Password(
                Constants.TYPE_KNOCK_CODE, getString(R.string.knock_code), R.drawable.ic_knock, 6
            )
        )
        currentType = listPasswordTypes.first()
        switchType(currentType)
    }

    override fun initUI() {
        binding.tvPasswordType.text = currentType?.name
        binding.layoutKnockCode.knockView.setIndicator(binding.layoutKnockCode.knockIndicator)
    }

    override fun initListener() {
        binding.btnChoose.setOnClickListener(this)
        binding.btnCreate.setOnClickListener(this)
        binding.btnConfirm.setOnClickListener(this)
        binding.tvReset.setOnClickListener(this)
        binding.layoutPinCode.pinCodeView.setThemeId(ThemeData.DEFAULT_THEME_ID)
        binding.layoutKnockCode.knockView.setThemeId(ThemeData.DEFAULT_THEME_ID)
        binding.layoutPatternCode.patternView.setThemeId(ThemeData.DEFAULT_THEME_ID)
    }

    override fun onViewClicked(view: View) {
        when (view.id) {
            R.id.btnChoose -> {
                showDialogSelectType()
            }

            R.id.btnCreate -> {
                finishStepOne()
            }

            R.id.btnConfirm -> {
                finishStepTwo()
            }

            R.id.tvReset -> {
                resetInput()
            }
        }
    }

    private fun finishStepTwo() {
        if (password == confirmPassword) {
            mainViewModel.savePassword(currentType, password)
            mainViewModel.saveLockType(currentType)
            FirebaseEvent.createLock(currentType?.name.toString())
            findNavController().popBackStack(R.id.setupScreen, true)
            findNavController().navigate(R.id.suggestLockScreen)
        } else {
            binding.btnConfirm.visibility = View.GONE
            binding.btnCreate.visibility = View.GONE
            binding.tvMessage.visibility = View.VISIBLE
            binding.tvMessage.text = getString(R.string.password_does_not_match)
            binding.layoutPinCode.pinCodeView.clearPinCode()
            binding.layoutKnockCode.knockView.clearClicks()
            binding.layoutPatternCode.patternView.reset()
        }
    }

    private fun finishStepOne() {
        binding.btnCreate.visibility = View.GONE
        binding.layoutPinCode.pinCodeView.clearPinCode()
        binding.layoutKnockCode.knockView.clearClicks()
        binding.layoutPatternCode.patternView.reset()
        binding.imgStepTwo.setImageResource(R.drawable.ic_step_two_selected)
        switchStep(currentType)
        step = Step.Step2
    }

    private fun showDialogSelectType() {
        val dialog = DialogSelectTypePassword.newInstance(listPasswordTypes.toTypedArray())
        if (!dialog.isShown) {
            dialog.show(childFragmentManager, "")
        }
        dialog.callback = {
            currentType = it
            binding.tvPasswordType.text = currentType?.name
            resetInput()
        }
    }

    private fun switchType(password: Password?) {
        resetState()
        when (password?.id) {
            Constants.TYPE_PIN_4_digit -> {
                binding.layoutPinCode.tvTitle.text = context?.getString(R.string.create_4_digit_pin)
                binding.layoutPinCode.pinCodeView.changePinLength(4)
                binding.layoutPinCode.root.visibility = View.VISIBLE
            }

            Constants.TYPE_PIN_6_digit -> {
                binding.layoutPinCode.tvTitle.text = context?.getString(R.string.create_6_digit_pin)
                binding.layoutPinCode.pinCodeView.changePinLength(6)
                binding.layoutPinCode.root.visibility = View.VISIBLE
            }

            Constants.TYPE_KNOCK_CODE -> {
                binding.layoutKnockCode.tvTitle.text =
                    context?.getString(R.string.create_4_6_touches)
                binding.layoutKnockCode.root.visibility = View.VISIBLE
            }

            Constants.TYPE_PATTERN -> {
                binding.layoutPatternCode.root.visibility = View.VISIBLE
                binding.layoutPatternCode.tvTitle.text = context?.getString(R.string.create_pattern)
            }
        }
    }

    private fun switchStep(password: Password?) {
        when (password?.id) {
            Constants.TYPE_PIN_4_digit -> {
                binding.layoutPinCode.tvTitle.text =
                    context?.getString(R.string.confirm_4_digit_pin)
            }

            Constants.TYPE_PIN_6_digit -> {
                binding.layoutPinCode.tvTitle.text =
                    context?.getString(R.string.confirm_6_digit_pin)
            }

            Constants.TYPE_KNOCK_CODE -> {
                binding.layoutKnockCode.tvTitle.text = context?.getString(R.string.confirm_knock)
            }

            Constants.TYPE_PATTERN -> {
                binding.layoutPatternCode.tvTitle.text =
                    context?.getString(R.string.confirm_pattern)
            }
        }
    }

    private fun resetState() {
        step = Step.Step1
        binding.layoutPinCode.pinCodeView.clearPinCode()
        password = ""
        confirmPassword = ""
        binding.layoutPinCode.root.visibility = View.GONE
        binding.layoutKnockCode.root.visibility = View.GONE
        binding.layoutPatternCode.root.visibility = View.GONE
        binding.tvMessage.visibility = View.GONE
        binding.btnConfirm.visibility = View.GONE
        binding.btnCreate.visibility = View.GONE
    }

    private fun resetInput() {
        binding.layoutPinCode.pinCodeView.clearPinCode()
        binding.layoutKnockCode.knockView.clearClicks()
        binding.layoutPatternCode.patternView.reset()
        binding.tvMessage.visibility = View.GONE
        binding.btnConfirm.visibility = View.GONE
        binding.btnCreate.visibility = View.GONE
        binding.imgStepTwo.setImageResource(R.drawable.img_step_two_normal)
        step = Step.Step1
        password = ""
        confirmPassword = ""
        switchType(currentType)
    }

    override fun loadAds() {
        activity?.let { act ->
            CemAdManager.getInstance(act).loadBannerAndShowByActivity(
                activity = act,
                viewGroup = binding.layoutBanner.bannerLayout,
                configKey = ConstAd.BANNER_KEY_HOME,
                position = ConstAd.POS_BOTTOM_BANNER,
                nameScreen = this::class.simpleName,
                callback = object : BannerAdListener {
                    override fun onBannerLoaded(banner: BannerAdView, view: View) { }

                    override fun onBannerFailed(error: String?) {
                        Log.d("onBannerFailed", "$error")
                    }

                    override fun onBannerClicked() {}

                    override fun onBannerOpen() {}

                    override fun onBannerClose() {}

                }
            )
        }
    }
}