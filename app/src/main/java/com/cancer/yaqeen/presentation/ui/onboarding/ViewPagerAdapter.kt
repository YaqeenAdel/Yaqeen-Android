package com.cancer.yaqeen.presentation.ui.onboarding

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cancer.yaqeen.data.features.onboarding.models.Photo

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private var pages: List<Fragment> = listOf()
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int =
        pages.size


    override fun createFragment(position: Int): Fragment =
        pages[position]

}