package com.cancer.yaqeen.presentation.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentOnBoardingBinding
import com.google.android.material.tabs.TabLayout


class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnBoardingBinding? = null
    private var binding: FragmentOnBoardingBinding? = _binding

    private lateinit var navController: NavController

    private lateinit var adapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        binding?.tabLayout?.apply {
            addTab(newTab().setText("1"))
            addTab(newTab().setText("2"))
        }
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

        val adapter =
            ViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle, pages, itemList)
        binding?.viewPager?.apply {
            this.adapter = adapter
            setPadding(50, 0, 50, 0)
            clipToPadding = false
        }

        binding?.apply {
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        binding!!.viewPager.currentItem = tab.position
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}

            })

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding!!.tabLayout.selectTab(binding!!.tabLayout.getTabAt(position))
                }
            })
        }
        Toast.makeText(requireContext(), "Hello android", Toast.LENGTH_SHORT).show()


    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}