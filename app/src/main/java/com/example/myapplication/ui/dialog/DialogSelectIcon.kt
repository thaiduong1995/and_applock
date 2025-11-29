package com.example.myapplication.ui.dialog

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.cem.admodule.enums.AdNetwork
import com.cem.admodule.ext.ConstAd
import com.cem.admodule.inter.InterstitialShowCallback
import com.cem.admodule.manager.CemAdManager
import com.example.myapplication.R
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.data.model.Icon
import com.example.myapplication.databinding.DialogSelectIconBinding
import com.example.myapplication.extention.FirebaseEvent
import com.example.myapplication.extention.setRadius
import com.example.myapplication.ui.adapter.IconAdapter
import com.example.myapplication.utils.IntentHelper
import com.example.myapplication.utils.PermissionChecker
import com.example.myapplication.view_model.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DialogSelectIcon : BottomSheetDialogFragment() {

    private lateinit var binding: DialogSelectIconBinding
    private lateinit var adapter: IconAdapter
    private val mainViewModel: MainViewModel by activityViewModels()
    private val listIcon = ArrayList<Icon>()
    private var selectedIcon: Bitmap? = null
    private var isType: Boolean = true
    var onSaveIcon: (Bitmap?) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogSelectIconBinding.inflate(inflater, container, false)
        initData()
        initUI()
        initListener()
        initObserver()
        return binding.root
    }


    private fun initData() {
        listIcon.add(Icon(R.drawable.ic_calculate, true))
        listIcon.add(Icon(R.drawable.ic_gallery))
        listIcon.add(Icon(R.drawable.ic_gallery_beauti))
        listIcon.add(Icon(R.drawable.ic_timer))
        listIcon.add(Icon(R.drawable.ic_message))
        selectedIcon = BitmapFactory.decodeResource(resources, listIcon.first().resId)
    }

    private fun initUI() {
        adapter = IconAdapter(listIcon) {
            selectedIcon = BitmapFactory.decodeResource(resources, it.resId)
            isType = true
            binding.imgCurrentPhoto.imgSelected.visibility = View.GONE
        }
        binding.rclDefaultIcon.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rclDefaultIcon.adapter = adapter

        binding.imgCurrentPhoto.imgIcon.setRadius(26)
    }

    private fun initListener() {
        binding.imgAdd.setOnClickListener {
            (activity as? BaseActivity)?.let {
                if (PermissionChecker.isHaveStoragePermission(it)) {
                    selectImageOnDevice()
                } else {
                    PermissionChecker.requestStoragePermission(it)
                }
            }
        }

        binding.tvSave.setOnClickListener {
            FirebaseEvent.saveIcon(isType)
            activity?.let { act ->
                CemAdManager.getInstance(requireActivity()).showInterstitial(
                    activity = act,
                    configKey = ConstAd.FULL_KEY_DETAIL,
                    callback = object : InterstitialShowCallback {
                        override fun onAdFailedToShowCallback(error: String) {
                            onSaveIcon.invoke(selectedIcon)
                            dismiss()
                        }

                        override fun onAdShowedCallback(network: AdNetwork) {
                            onSaveIcon.invoke(selectedIcon)
                            dismiss()
                        }

                        override fun onDismissCallback(network: AdNetwork) {
                            onSaveIcon.invoke(selectedIcon)
                            dismiss()
                        }

                        override fun onAdClicked() {
                            onSaveIcon.invoke(selectedIcon)
                            dismiss()
                        }

                    }

                )
            }
        }
    }

    private fun initObserver() {
        mainViewModel.readExternalStoragePermissionLiveData.observe(this) { granted ->
            if (granted) {
                selectImageOnDevice()
            } else {
                // do nothing
            }
        }
    }

    private fun selectImageOnDevice() {
        startForResult.launch(IntentHelper.pickImageIntent())
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            context?.let { ct ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data?.data
                    if (context != null && data != null) {
                        Glide.with(requireContext()).asBitmap().load(data).override(200, 200)
                            .centerCrop().into(object : CustomTarget<Bitmap>() {
                                override fun onResourceReady(
                                    resource: Bitmap, transition: Transition<in Bitmap>?
                                ) {
                                    selectedIcon = resource
                                    isType = false
                                    binding.imgCurrentPhoto.imgIcon.setImageBitmap(resource)
                                    binding.imgCurrentPhoto.imgSelected.visibility = View.VISIBLE
                                    adapter.clearSelection()
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {

                                }
                            })

                    }
                }
            }
        }

    companion object {

        @JvmStatic
        fun newInstance() = DialogSelectIcon().apply {
            arguments = bundleOf()
        }
    }
}