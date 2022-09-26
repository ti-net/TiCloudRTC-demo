package com.example.rtc_android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.rtc_android.MainActivityViewModel
import com.example.rtc_android.R
import com.example.rtc_android.databinding.FragmentShowBinding
import com.google.android.material.tabs.TabLayoutMediator

class ShowFragment : Fragment() {

    private lateinit var binding: FragmentShowBinding
    private val tabTextList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabTextList.apply {
            add(requireContext().resources.getString(R.string.out_call_scene))
            add(requireContext().resources.getString(R.string.hotline_scene))
        }

        binding.apply {
            viewPager2.adapter = ShowPageFragmentStateAdapter()
        }

        TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager2,
            true
        ) { tab, position ->
            tab.text = tabTextList[position]
        }.attach()
    }

    inner class ShowPageFragmentStateAdapter : FragmentStateAdapter(this) {
        private val subFragments = mutableListOf<Fragment>()

        init {
            subFragments.apply {
                add(OutCallFragment())
                add(HotlineFragment())
            }
        }

        override fun getItemCount(): Int = subFragments.size

        override fun createFragment(position: Int): Fragment = subFragments[position]

    }
}