package com.example.myapplication.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

abstract class BaseCacheFragment<T : ViewBinding> : BaseFragment() {

    lateinit var binding: T
    private var isFirstCreateView = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        if (!this::binding.isInitialized) {
            binding = createView(inflater, container)
            loadAds()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isFirstCreateView) {
            getDataBundle()
            initData()
            initUI()
            initListener()
            initObservers()
            checkPermissions()
            isFirstCreateView = false
        }
    }

    abstract fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): T
}