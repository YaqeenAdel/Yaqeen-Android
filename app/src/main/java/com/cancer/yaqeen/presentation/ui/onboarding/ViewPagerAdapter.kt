package com.cancer.yaqeen.presentation.ui.onboarding

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val pages: List<Fragment> = listOf(),
    private val itemList: List<Int> = listOf()
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int =
        itemList.size


    override fun createFragment(position: Int): Fragment =
        pages[position]
}