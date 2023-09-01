package com.cancer.yaqeen.presentation.ui.onboarding

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentOnBoardingBinding
import com.cancer.yaqeen.presentation.ui.auth.AuthViewModel
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.autoScroll
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OnBoardingFragment : Fragment(), OnClickListener {

    private var binding: FragmentOnBoardingBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var adapter: ViewPagerAdapter

    private val authViewModel: AuthViewModel by viewModels()

    private val handler = Handler()
    private var scrollPosition = 0

    val runnable = object : Runnable {

        override fun run() {

            /**
             * Calculate "scroll position" with
             * adapter pages count and current
             * value of scrollPosition.
             */
            val count = adapter.itemCount
            binding.viewPager.setCurrentItem(scrollPosition++ % count, true)

            handler.postDelayed(this, 3000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setupViewPager()
        setListener()
    }

    private fun setListener() {
        binding.tvLanguage.setOnClickListener(this)
        binding.btnExploreApp.setOnClickListener(this)
        binding.btnJoin.setOnClickListener(this)
        binding.tvLogin.setOnClickListener(this)

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    binding.viewPager.currentItem = tab.position
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })
    }

    private fun setupViewPager() {
        val itemList = listOf(
            R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background
        )

        val pages = listOf<Fragment>(
            PageFragment(),
            PageFragment(),
            PageFragment()
        )

        adapter =
            ViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle, pages, itemList)

        binding.viewPager.apply {
            this.adapter = this@OnBoardingFragment.adapter
            clipToPadding = false
        }
        binding.tabLayout.apply {
            addTab(newTab())
            addTab(newTab())
            addTab(newTab())
        }

        binding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                scrollPosition = position + 1
            }
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}
        })

        handler.post(runnable)
    }

    private fun removeCallbacks(){
        binding.viewPager.removeCallbacks(runnable)
        handler.removeCallbacks(runnable)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tv_language -> {}
            R.id.btn_explore_app -> {}
            R.id.btn_join -> {
                removeCallbacks()
                navController.tryNavigate(
                    OnBoardingFragmentDirections.actionOnBoardingFragmentToIntroFragment()
                )
            }
            R.id.tv_login -> {
                authViewModel.login()
            }
        }
    }

}