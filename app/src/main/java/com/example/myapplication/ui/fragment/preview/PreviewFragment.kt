package com.example.myapplication.ui.fragment.preview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R
import com.example.myapplication.base.BaseCacheFragment
import com.example.myapplication.databinding.FragmentPreviewBinding
import com.example.myapplication.ui.adapter.CommonPagerAdapter

class PreviewFragment : BaseCacheFragment<FragmentPreviewBinding>() {

    private var listFragment = mutableListOf<Fragment>()
    private var adapter: CommonPagerAdapter? = null

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPreviewBinding {
        return FragmentPreviewBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        listFragment.add(PreviewOneFragment())
        listFragment.add(PreviewTwoFragment())
        listFragment.add(PreviewThreeFragment())
    }

    override fun initUI() {
        initViewPagerAdapter()
    }

    override fun initListener() {
        binding.tvSkip.setOnClickListener(this)
        binding.imgNext.setOnClickListener(this)
    }

    private fun initViewPagerAdapter() {
        adapter = CommonPagerAdapter(childFragmentManager, lifecycle)
        adapter?.setListFragment(listFragment)
        binding.viewpager.adapter = adapter
        binding.viewpager.offscreenPageLimit = listFragment.size
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 2) {
                    binding.tvSkip.visibility = View.GONE
                    binding.imgNext.visibility = View.GONE
                } else {
                    binding.tvSkip.visibility = View.VISIBLE
                    binding.imgNext.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onViewClicked(view: View) {
        when (view.id) {
            R.id.tvSkip -> {
                findNavController().popBackStack(R.id.previewScreen, true)
                findNavController().navigate(R.id.setupScreen)
            }

            R.id.imgNext -> {
                binding.viewpager.currentItem += 1
            }
        }
    }
}