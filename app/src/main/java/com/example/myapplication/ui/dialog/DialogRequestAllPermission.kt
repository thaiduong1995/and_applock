package com.example.myapplication.ui.dialog

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.myapplication.R
import com.example.myapplication.databinding.DialogRequestAllPermissionBinding
import com.example.myapplication.extention.FirebaseEvent
import com.example.myapplication.utils.IntentHelper
import com.example.myapplication.utils.PermissionChecker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DialogRequestAllPermission : BottomSheetDialogFragment() {

    private var _binding: DialogRequestAllPermissionBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogRequestAllPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        initViewPermission()

        binding.tvGoToSetting.setOnClickListener {
            context?.let { ct ->
                when {
                    !PermissionChecker.checkUsageAccessPermission(ct) -> startForResult.launch(
                        IntentHelper.usageAccessIntent()
                    )

                    !PermissionChecker.checkOverlayPermission(ct) -> startForResult.launch(
                        IntentHelper.overlayIntent(ct.packageName)
                    )

                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                        if (!PermissionChecker.isNotificationPost(ct)) {
                            startNotificationPost.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }

                    PermissionChecker.isAllPermissionChecked(ct) -> {
                        Handler(Looper.getMainLooper()).postDelayed({
                            dismiss()
                        }, 500)
                    }
                }
            }
        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            context?.let { ct ->
                initViewPermission()

                if (PermissionChecker.isAllPermissionChecked(ct)) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        dismiss()
                    }, 500)
                }
            }
        }

    private val startNotificationPost =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            context?.let { ct ->
                initViewPermission()

                if (PermissionChecker.isAllPermissionChecked(ct)) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        dismiss()
                    }, 500)
                }
            }
        }

    private fun initViewPermission() {
        context?.let { ct ->
            if (PermissionChecker.checkUsageAccessPermission(ct)) {
                binding.imgUsageAccessEnable.setImageResource(R.drawable.ic_enable)
            } else {
                binding.imgUsageAccessEnable.setImageResource(R.drawable.ic_disable)
            }

            if (PermissionChecker.checkOverlayPermission(ct)) {
                binding.imgDrawOtherAppEnable.setImageResource(R.drawable.ic_enable)
            } else {
                binding.imgDrawOtherAppEnable.setImageResource(R.drawable.ic_disable)
            }

            if (PermissionChecker.isNotificationPost(ct)) {
                FirebaseEvent.fillShowUp()
                binding.imgNotificationEnable.setImageResource(R.drawable.ic_enable)
            } else {
                binding.imgNotificationEnable.setImageResource(R.drawable.ic_disable)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance() = DialogRequestAllPermission().apply {
            arguments = bundleOf()
        }
    }
}