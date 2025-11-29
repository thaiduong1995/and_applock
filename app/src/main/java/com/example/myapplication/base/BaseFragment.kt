package com.example.myapplication.base

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber

/**
 * Created by Thinhvh on 23/09/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
open class BaseFragment : Fragment(), View.OnClickListener {

    var TAG: String = javaClass.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("____Activity" + this@BaseFragment::class.java)
    }

    fun popBackStack() {
        try {
            findNavController().popBackStack()
        } catch (e: Exception) {
            parentFragmentManager.popBackStack()
        }
    }

    open fun loadAds() {}
    open fun getDataBundle() {}
    open fun initData() {}
    open fun initUI() {}
    open fun initListener() {}
    open fun initObservers() {}
    open fun onViewClicked(view: View) {}
    open fun checkPermissions() {}
    override fun onClick(view: View?) {
        view?.let {
            onViewClicked(it)
        }
    }

    fun setNavGraphId(navGraphId: Int) {
        (activity as? BaseActivity)?.setNavGraphId(navGraphId)
    }

    open fun dispatchTouchEvent(event: MotionEvent) {}

    fun showBottomSheetDialogFragment(fragment: BottomSheetDialogFragment?) {
        if (fragment == null || fragment.isAdded) {
            return
        }
        fragment.show(requireActivity().supportFragmentManager, fragment.tag)
    }

    fun showDialogFragment(fragment: DialogFragment?) {
        if (fragment == null || fragment.isAdded) {
            return
        }
        fragment.show(requireActivity().supportFragmentManager, fragment.tag)
    }

    override fun onResume() {
        super.onResume()
        loadAds()
    }
}