package com.example.myapplication.ui.fragment

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.BannerAdListener
import com.cem.admodule.inter.BannerAdView
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.data.model.LockType
import com.example.myapplication.data.model.liveData.StateData.DataStatus.ERROR
import com.example.myapplication.data.model.liveData.StateData.DataStatus.LOADING
import com.example.myapplication.data.model.liveData.StateData.DataStatus.SUCCESS
import com.example.myapplication.databinding.FragmentDetailsCustomThemeBinding
import com.example.myapplication.extention.FirebaseEvent
import com.example.myapplication.extention.gone
import com.example.myapplication.extention.setRadius
import com.example.myapplication.extention.toastMessageShortTime
import com.example.myapplication.extention.visible
import com.example.myapplication.ui.custom.toPx
import com.example.myapplication.ui.dialog.SelectColorDialog
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.PermissionChecker
import com.example.myapplication.utils.Utils
import com.example.myapplication.view_model.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsCustomThemeFragment : BaseCacheFragment<FragmentDetailsCustomThemeBinding>(),
    CropImageView.OnCropImageCompleteListener {

    private val viewModel by viewModels<ThemeViewModel>()
    private var lockType = LockType.PATTERN
    private var backgroundBitmap: Bitmap? = null
    private var lineColor: Int = Color.parseColor("#C4CDE2")
    private var dotsColor: Int = Color.parseColor("#C4CDE2")
    private var knockColor: Int = Color.parseColor("#C4CDE2")
    private var numberColor: Int = Color.parseColor("#1C1F33")
    private val cropImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            binding.cropImage.root.visible()
            binding.cropImage.cropImageView.setImageUriAsync(result)
        }

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentDetailsCustomThemeBinding {
        return FragmentDetailsCustomThemeBinding.inflate(inflater, container, false)
    }

    override fun getDataBundle() {
        lockType = arguments?.getSerializable(Constants.KEY_TYPE_PASSWORD) as LockType
    }

    override fun initUI() {
        when (lockType) {
            LockType.KNOCK -> {
                binding.btnDots.visibility = View.GONE
                binding.btnLineColor.visibility = View.GONE
                binding.btnNumberColor.visibility = View.GONE
                binding.btnKnockColor.visibility = View.VISIBLE
            }

            LockType.PATTERN -> {
                binding.btnKnockColor.visibility = View.GONE
                binding.btnNumberColor.visibility = View.GONE
            }

            LockType.PASS_CODE -> {
                binding.btnDots.visibility = View.GONE
                binding.btnKnockColor.visibility = View.GONE
                binding.btnLineColor.visibility = View.GONE
                binding.btnNumberColor.visibility = View.GONE
                binding.btnNumberColor.visibility = View.VISIBLE
            }
        }

        binding.previewView.setLockType(lockType)

        binding.imgPreviewBackground.setRadius(16f.toPx.toInt())
        binding.imgPreviewDotColor.setRadius(16f.toPx.toInt())
        binding.imgLineColorPreview.setRadius(16f.toPx.toInt())
        binding.imgKnockColorPreview.setRadius(16f.toPx.toInt())
        binding.imgNumberColorPreview.setRadius(16f.toPx.toInt())
        binding.cropImage.cropImageView.setImageCropOptions(CropImageOptions())
        binding.cropImage.cropImageView.setOnCropImageCompleteListener(this)
    }

    override fun initListener() {
        binding.imgBack.setOnClickListener(this)
        binding.tvCreate.setOnClickListener(this)
        binding.btnDots.setOnClickListener(this)
        binding.btnBackground.setOnClickListener(this)
        binding.btnLineColor.setOnClickListener(this)
        binding.btnKnockColor.setOnClickListener(this)
        binding.btnNumberColor.setOnClickListener(this)
        binding.cropImage.apply {
            txtCancel.setOnClickListener {
                this.root.gone()
            }
            txtDone.setOnClickListener {
                cropImageView.croppedImageAsync()
            }
            txtRotate.setOnClickListener {
                cropImageView.rotateImage(90)
            }
        }
    }

    override fun onViewClicked(view: View) {
        when (view.id) {
            R.id.imgBack -> {
                popBackStack()
            }

            R.id.tvCreate -> {
                if (backgroundBitmap != null) {
                    FirebaseEvent.clickCustom(lockType.name)
                    viewModel.addCustomTheme(
                        backgroundBitmap!!,
                        getPreviewBitmap(),
                        lockType,
                        dotsColor,
                        numberColor,
                        knockColor,
                        lineColor
                    )
                } else {
                    context?.toastMessageShortTime(getString(R.string.you_must_select_background))
                }
            }

            R.id.btnDots -> {
                showDialogSelectColor(R.id.btnDots)
            }

            R.id.btnBackground -> {
                selectImage()
            }

            R.id.btnLineColor -> {
                showDialogSelectColor(R.id.btnLineColor)
            }

            R.id.btnKnockColor -> {
                showDialogSelectColor(R.id.btnKnockColor)
            }

            R.id.btnNumberColor -> {
                showDialogSelectColor(R.id.btnNumberColor)
            }
        }
    }

    private fun getPreviewBitmap(): Bitmap {
        return Utils.getBitmapFromViewGroup(binding.previewView)
    }

    private fun showDialogSelectColor(viewId: Int) {
        val dialog = SelectColorDialog.newInstance()
        if (!dialog.isShown) {
            dialog.show(childFragmentManager, "")
        }
        dialog.onClickColor = {
            when (viewId) {
                R.id.btnDots -> {
                    binding.imgPreviewDotColor.setBackgroundColor(it)
                    binding.imgDotsChecked.visibility = View.VISIBLE
                    dotsColor = it
                }

                R.id.btnLineColor -> {
                    binding.imgLineColorPreview.setBackgroundColor(it)
                    binding.imgLineColorChecked.visibility = View.VISIBLE
                    lineColor = it
                }

                R.id.btnKnockColor -> {
                    binding.imgKnockColorPreview.setBackgroundColor(it)
                    binding.imgKnockColorChecked.visibility = View.VISIBLE
                    knockColor = it
                }

                R.id.btnNumberColor -> {
                    binding.imgNumberColorPreview.setBackgroundColor(it)
                    binding.imgNumberColorChecked.visibility = View.VISIBLE
                    numberColor = it
                }
            }
            binding.previewView.setBackgroundBitmap(backgroundBitmap)
            binding.previewView.setDotColor(dotsColor)
            binding.previewView.setKnockColor(knockColor)
            binding.previewView.setLineColor(lineColor)
            binding.previewView.setNumberColor(numberColor)
        }
    }

    private fun selectImage() {
        activity?.let { activity ->
            if (PermissionChecker.isHaveStoragePermission(activity)) {
                openGallery()
            } else {
                launchWriteExternal.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private var launchWriteExternal =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                openGallery()
            }
        }

    private fun openGallery() {
        cropImage.launch("image/*")
    }

    override fun initObservers() {
        viewModel.saveThemeStateLiveData.observe(this) {
            when (it.getStatus()) {
                LOADING -> {
                    binding.tvCreate.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE
                }

                SUCCESS -> {
                    binding.tvCreate.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    context?.toastMessageShortTime(getString(R.string.custom_theme_created))
                    findNavController().popBackStack(R.id.detailsCustomThemeScreen, true)
                    findNavController().popBackStack(R.id.customThemeScreen, true)
                    findNavController().navigate(R.id.myLibraryScreen)
                }

                ERROR -> {
                    binding.tvCreate.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                }

                else -> {}
            }
        }
    }

    override fun onCropImageComplete(view: CropImageView, result: CropImageView.CropResult) {
        if (result.isSuccessful) {
            val selectedImage = result.bitmap
            binding.imgPreviewBackground.setImageBitmap(selectedImage)
            binding.imgBackgroundChecked.visibility = View.VISIBLE
            backgroundBitmap = selectedImage
            binding.previewView.setBackgroundBitmap(backgroundBitmap)
            binding.cropImage.root.gone()
        } else {
            context?.toastMessageShortTime(getString(R.string.common_error))
        }
    }

    override fun loadAds() {
        activity?.let { act ->
            CemAdManager.getInstance(act).loadBannerAndShowByActivity(
                activity = act,
                viewGroup = binding.layoutBanner.bannerLayout,
                configKey = ConstAd.BANNER_KEY_DETAIL,
                position = ConstAd.POS_BOTTOM_BANNER,
                nameScreen = this::class.simpleName,
                callback = object : BannerAdListener {
                    override fun onBannerLoaded(banner: BannerAdView, view: View) {}

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